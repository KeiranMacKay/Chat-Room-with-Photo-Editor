package com.example.wschatserverdemo;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

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
            String stage = jsonmsg.getString("stage");
            if(stage.equals("begin")){
                // This triggers at the start of an upload and defines the image size.
                String[] conv = message.split(":");
                int imgwidth = Integer.parseInt(conv[0]);
                int imheight = Integer.parseInt(conv[1]);
                String name = conv[2];
                session.getBasicRemote().sendText("{\"type\": \"chat\", \"message\": \"" + imheight + "::" + imgwidth + "\"}");
            }else{
                // block that recieves data from upload.
                // data is recieved in the form <int>*<int>*<int> as a string.

                // so, to get a String[] of stuff we need to cast to ints..:
                String[] convert = message.split("-");
                List<Integer> convInt = new ArrayList<Integer>();
                for(String n : convert){
                    convInt.add(Integer.parseInt(n.trim()));
                }
                // we now have an array of ints that we need to convert into an image file.
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