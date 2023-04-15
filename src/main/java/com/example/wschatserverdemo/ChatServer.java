package com.example.wschatserverdemo;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;


@ServerEndpoint(value="/ws/{roomID}") // websocket endpoint
public class ChatServer {

    private Map<String, String> usernames = new HashMap<String, String>();
    private static Map<String,String> roomList = new HashMap<String,String>();

    @OnOpen
    public void open(@PathParam("roomID") String roomID, Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server ): Welcome to the chat room. Please state your username to begin.\"}");
        roomList.put(session.getId(),roomID); // associate userID with roomID in hashMap. (different userIDs can yield same roomID)

    }

    @OnClose
    public void close(Session session) throws IOException, EncodeException {
        String userId = session.getId();
        String roomID = roomList.get(userId);
        if (usernames.containsKey(userId)) {
            String username = usernames.get(userId);
            usernames.remove(userId);
            for (Session peer : session.getOpenSessions()){ //broadcast this person left the server
                if(roomList.get(peer.getId()).equals(roomID)) {
                    peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server): " + username + " left the chat room.\"}");
                }
            }
        }
    }
    @OnMessage
    public void handleMessage(String message, Session session) throws IOException, EncodeException {
        // this seems a little strange, but at some point I may have to handle non-JSON messages,
        // so i've cracked the code for JSONs into its own function.
        handleJson(message,session);
    }
    private void handleJson(String comm, Session session) throws IOException, EncodeException{
        // working code that handles JSON type messages.
        String userID = session.getId(); // users' ID
        String roomID = roomList.get(userID); // specific room
        JSONObject jsonmsg = new JSONObject(comm); // grab string from message json
        String type = jsonmsg.getString("type");
        String message = jsonmsg.getString("msg");
        System.out.println(type + " " + message);

        // look for file requests..:
        if(type.equals("file")){
            // data members
            String name = "";
            int imgwidth = 0;
            int imheight = 0;
            List<Integer> vals = new ArrayList<Integer>();

            String stage = jsonmsg.getString("stage");
            if(stage.equals("begin")){
                // This triggers at the start of an upload and defines the image size and name.
                String[] conv = message.split(":");
                imgwidth = Integer.parseInt(conv[0]);
                imheight = Integer.parseInt(conv[1]);
                name = conv[2];
                session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\"" + "Attempting image construction2" + imgwidth +" "+ imheight + "\"}");
            }else{
                // block that recieves data from upload.
                // data is recieved in the form <int>*<int>*<int> as a string.

                // so, to get a String[] of stuff we need to cast to ints..:
                String[] convert = message.split("-");
                for(String n : convert){
                    vals.add(Integer.parseInt(n.trim()));
                }
                // cast List<Int> to int[]..:
                int[] valsf = vals.stream().mapToInt(Integer::intValue).toArray();
                session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\"" + "image received" + "" + "\"}");
                // construct our jpeg...
                BufferedImage bImage = null;
                session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\"" + "Attempting image construction" + imgwidth +" "+ imheight + "\"}");
                BufferedImage image = new BufferedImage(imgwidth,imheight,BufferedImage.TYPE_INT_RGB);
                File output = new File("test2.jpg");
                session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\""+"Creating image..: " + image.toString() + "" + "\"}");
                try{
                    ImageIO.write(image,"JPEG",output);
                    session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\""+"Creating image..: " + "SUCCESS" + "" + "\"}");
                }catch(Exception e){
                    e.printStackTrace();
                    session.getBasicRemote().sendText("{\"type\": \"sys_debug\", \"message\":\""+"Creating image..: " + e.toString() + "" + "\"}");
                }
            }
        }
        // look for refresh type messages..:
        else if(type.equals("refresh")){
            if(message.equals("rooms")) {
                String packer = "";
                Set<String> filter = new HashSet<String>();
                // don't want to display duplicate rooms..:
                for(String val : roomList.values()){
                    filter.add(val);
                }
                // whack them in a string..:
                for(String val : filter){
                    packer += val + "*";
                }
                session.getBasicRemote().sendText("{\"type\": \"roomList\", \"message\":\"" + packer + "" + "\"}");
            }
        }
        // look for chat type messages..:
        else if(type.equals("chat")) {
            if (usernames.containsKey(userID)) { // not their first message
                String username = usernames.get(userID);
                System.out.println(username);
                // for peer in all peers...:
                for (Session peer : session.getOpenSessions()) {
                    // only if they're in our room! Secret!
                    if (roomList.get(peer.getId()).equals(roomID)) {
                        peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(" + username + "): " + message + "\"}");
                    }
                }
            } else { //first message is their username
                usernames.put(userID, message);
                session.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server ): Welcome, " + message + "!\"}");
                for (Session peer : session.getOpenSessions()) {
                    // only print out to users with same roomID as the user that sends the message, excluding user.
                    if ((!peer.getId().equals(userID)) && (roomList.get(peer.getId()).equals(roomID))) {
                        peer.getBasicRemote().sendText("{\"type\": \"chat\", \"message\":\"(Server): " + message + " joined the chat room.\"}");
                    }
                }
            }
        }
    }
}