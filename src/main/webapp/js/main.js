/*
    This file contains a lot of functional programming
    which is what makes the server run.
    This file implements all the onClick button scripts,
    aswell as handling client-side server connectivity.

 */

// enters the room for a given code (should be a string)
// if no code given, tries to grab it from the room entry.
// otherwise, doesn't do anything.

let ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws/default");
function enterRoom(code) {
    if(code == null){
        let code = document.getElementById("room-code").value;
    }
    ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws/" + code);

//let  ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws"+code);

    ws.onmessage = function (event) {
        console.log(event.data);
        let message = JSON.parse(event.data);
        if(message.type == 'roomList'){
            let pE = document.querySelector("#roomList");
            let items = pE.querySelectorAll("li,lu");
            items.forEach(function(item){
                item.parentNode.removeChild(item);
            });
            let split = message.message.split("*");
            for(x in split){
                var node = document.createElement('li');
                node.appendChild(document.createTextNode(split[x]));
                document.querySelector('ul').appendChild(node);
            }
        }
        if(message.type == 'chat'){
            document.getElementById("log").value += "[" + timestamp() + "] " + message.message + "\n";
        }
    }
}

// handles opening default connection..:
function load_default(){
    enterRoom("default");
}

// handles using enter to send a message
document.getElementById("input").addEventListener("keyup", function (event) {
    if (event.keyCode === 13) {
        let request = {"type":"chat", "msg":event.target.value};
        ws.send(JSON.stringify(request));
        event.target.value = "";
    }
});

/*
    on buttons:
    This button works by sending a message to the backend.
    I've made these messages unique, and they need to be
    caught in the backend and parsed for meaning.
    for Kei, I've left you openings in the backend to fill
    with how exactly you'd like to package the map for Gremmy
    to serve to the front end. For now, it has a unique type
    and a more technical message
        ~ Sam
 */

// handles the 'refresh' button, getting roomList from backend
const button1 = document.querySelector('#refresh');
button1.addEventListener('click', () => {
    let request = {"type":"refresh","msg":"rooms"};
    ws.send(JSON.stringify(request));
});

// handles 'submit' button, sending message to server.
// this button directly runs a script, but otherwise
// operates the same way.
const button2 = document.querySelector('#submit');
button2.addEventListener('click', () =>{
    const inp = document.getElementById("input");
    let request = {"type":"chat", "msg":inp.value};
    ws.send(JSON.stringify(request));
    inp.value="";
});

// handles 'Create New Room' button, creating a new random room.
// button for creating a room.
// grabs a RoomID from the makeRandomID() function
// and then opens a websocket using that.
const button3 = document.querySelector('#create_room');
button3.addEventListener('click', () =>{
    let id = makeRandomID();
    enterRoom(id);
});

// handles submitting a file for better user feel.
/*
const buttonUp = document.querySelector('#up');
buttonUp.addEventListener('click', () =>{
    // grab our input field..:
    let inp = document.getElementById('jpg');
    let data = inp.file.item(0).getAsString();
    console.log(data);
});
 */

// get the time as a string from the client's computer.
function timestamp() {
    var d = new Date(), minutes = d.getMinutes();
    if (minutes < 10) minutes = '0' + minutes;
    return d.getHours() + ':' + minutes;
}

/*
    File Transfer notes:
    Following is the interface for uploading any file.
    Takes a file and turns it into a byte[] array datatype
    that is then transmitted to the server backend.
        ~ Sam

    pt.2:
    this might be easier through use of an input field and a button.
 */

document.getElementById("jpg").addEventListener("change",function(event) {
    // get the file and instantiate a reader..:
    let file = event.target.files[0];
    let reader = new FileReader();

    reader.onload = function(){
        // store the file as a byte array..:
        let data = new Uint8Array(reader.result);

        // we now construct a message in the format:
        // type:jpg message:<binary data>
        let request = {"type":"file","msg":data};
        ws.send(JSON.stringify(request));
        console.log(request.type + " File uploaded to server:" + request.msg);

    }
    reader.readAsArrayBuffer(file);
    document.getElementById("jpg").value = "";
});

// makes a random 8-digit alphanumeric room ID
function makeRandomID(){
    // function makes a room ID.
    let ID = '';
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let cnt = 0;
    let len = chars.length;
    // make 8 digit ID.
    while(cnt < 8){
        ID += chars.charAt(Math.floor(Math.random() * len));
        cnt += 1;
    }

    // Get the room codes to show up on the list on the webpage
    /*
    causes duplicate entries...
    var node = document.createElement('li');
    node.appendChild(document.createTextNode(ID));
    document.querySelector('ul').appendChild(node);
     */

    return ID;
}