//Get canvas element and context
const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');
let image; // Declare image in the global scope

//Set canvas to match image
function setImDim() {
    canvas.width = image.width;
    canvas.height = image.height;
    canvas.style.width = image.width + "px";
    canvas.style.height = image.height + "px";

    const offsetX = (canvas.width - image.width) / 2;
    const offsetY = (canvas.height - image.height) / 2;

    ctx.drawImage(image, offsetX, offsetY);
}


//Draw the image on the canvas
function drawImage() {
    ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
}

//Handle user drawing on the canvas
//Move down
function mouseDown(e) {
    canvas.addEventListener('mousemove', mouseMove);
    canvas.addEventListener('mouseup', mouseUp);

    const rect = canvas.getBoundingClientRect(); // Get canvas element's position relative to the viewport
    const x = e.clientX - rect.left; // Subtract canvas element's left position from clientX to get the relative position
    const y = e.clientY - rect.top; // Subtract canvas element's top position from clientY to get the relative position

    ctx.beginPath();
    ctx.moveTo(x, y);

    ctx.strokeStyle = document.getElementById('colorPicker').value;
}

//Move general
function mouseMove(e) {
    if (e.target == canvas) { // Only detect mouse movement within canvas
        const rect = canvas.getBoundingClientRect(); // Get canvas element's position relative to the viewport
        const x = e.clientX - rect.left; // Subtract canvas element's left position from clientX to get the relative position
        const y = e.clientY - rect.top; // Subtract canvas element's top position from clientY to get the relative position

        ctx.lineTo(x, y);
        ctx.stroke();
    }
}

//Move up
function mouseUp() {
    canvas.removeEventListener('mousemove', mouseMove);
    canvas.removeEventListener('mouseup', mouseUp);
}

//Clear canvas
function clearCanvas() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawImage();
}

//Handle user selecting an image
document.getElementById('imageFile').addEventListener('change', function() {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function(e) {
            image = new Image();
            image.src = e.target.result;
            image.onload = function() {
                setImDim();
                drawImage();
            };
        };
    }
});

function saveImage(canvas) {
    //Converts edit to PNG format
    var dataURL = canvas.toDataURL('image/png');

    //Create download link
    var link = document.createElement('a');
    link.download = 'edited-image.png';
    link.href = dataURL;

    //Downloads edited image
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

//For the button to call (idk why it wont work when i just call the main function but whatever)
function saveButtonHandler() {
    saveImage(canvas);
}


//Set up event listener for user drawing
canvas.addEventListener('mousedown', mouseDown);






