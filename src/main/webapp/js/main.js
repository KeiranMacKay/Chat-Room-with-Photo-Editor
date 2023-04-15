// establish websocket
let ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws/default");
function enterRoom(code) {
    if(code == null){
        let code = document.getElementById("room-code").value;
    }
    ws = new WebSocket("ws://localhost:8080/WSChatServerDemo-1.0-SNAPSHOT/ws/" + code);

    ws.onmessage = function (event) {
        console.log(event.data);
        let message = JSON.parse(event.data);

        // message filtering for types
        if(message.type == 'roomList'){
            let pE = document.querySelector("#roomList");
            let items = pE.querySelectorAll("li,lu");
            items.forEach(function(item){
                item.parentNode.removeChild(item);
            });
            let split = message.message.split("*");
            removeAll();
            for(x in split){
                var node = document.createElement("button");
                var node_2 = document.createElement("br");
                node.appendChild(document.createTextNode(split[x]));

                // create button for room codes
                node.id = "join_room";
                node.className = "room_button_2";
                node.onclick = function(){
                    enterRoom(split[x]);
                };

                //append button to list
                document.querySelector('ul').appendChild(node);
                document.querySelector('ul').appendChild(node_2);

            }
        }
        // could be an else if
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

// handles the 'refresh' button, getting roomList from backend
const button1 = document.querySelector('#refresh');
button1.addEventListener('click', () => {
    let request = {"type":"refresh","msg":"rooms"};
    ws.send(JSON.stringify(request));
});

// handles 'submit' button, sending message to server.
const button2 = document.querySelector('#submit');
button2.addEventListener('click', () =>{
    const inp = document.getElementById("input");
    let request = {"type":"chat", "msg":inp.value};
    ws.send(JSON.stringify(request));
    inp.value="";
});

// handles 'Create New Room' button, creating a new random room.
const button3 = document.querySelector('#create_room');
button3.addEventListener('click', () =>{
    let id = makeRandomID();
    enterRoom(id);
});

// get the time as a string from the client's computer.
function timestamp() {
    var d = new Date(), minutes = d.getMinutes();
    if (minutes < 10) minutes = '0' + minutes;
    return d.getHours() + ':' + minutes;
}

document.getElementById("jpg").addEventListener('change',function(event) {
    // get the file and instantiate a reader..:
    console.log("Uploading File...")
    let file = event.target.files[0];
    // grab dimensions of image..:
    const image = new Image();
    image.src = URL.createObjectURL(this.files[0]);
    image.onload = function () {
        // grab width and height
        let width = this.width;
        let height = this.height;
        // id for later...
        let id = makeRandomID();
        // shoot those to the front end aswell as the ID.
        let request = {"type":"file","msg":width+":"+height+":"+id,"stage":"begin"};
        ws.send(JSON.stringify(request));
    };

    let reader = new FileReader();

    reader.onload = function(){
        // make a blank payload
        let payload = ""
        let data = new Uint8Array(reader.result);
        for(x in data){
            // fill up the payload with a bunch of crap because we love java :)
            payload = payload + data[x].toString() + "-";
        }
        // shoot that off to the backend.
        let request = {"type":"file","msg":payload,"stage":"payload"};
        ws.send(JSON.stringify(request));
        console.log("Upload Complete");
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
    var node = document.createElement('li');
    node.appendChild(document.createTextNode(ID));
    document.querySelector('ul').appendChild(node);


    // create button for room codes
    let room_button = document.createElement('button');
    room_button.className = "room_button_1";
    room_button.id = "join_room";
    room_button.append("Join");
    room_button.onclick = function(){
        enterRoom(ID);
    };

    //append button to list
    document.querySelector('ul').appendChild(room_button);
    */


    // This version is for if u want the room codes to be the buttons themselves
    var node = document.createElement("button");
    var node_2 = document.createElement("br");
    node.appendChild(document.createTextNode(ID));

    // create button for room codes
    node.id = "join_room";
    node.className = "room_button_2";
    node.onclick = function(){
        enterRoom(ID);
    };

    //append button to list
    document.querySelector('ul').appendChild(node);
    document.querySelector('ul').appendChild(node_2);


   return ID;
}

// removes all elements in the list
function removeAll(){
    document.getElementById("roomList").innerHTML = "";
}