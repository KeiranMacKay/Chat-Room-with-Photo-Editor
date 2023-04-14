
        //Get canvas element and context
        const canvas = document.getElementById('canvas');
        const ctx = canvas.getContext('2d');

        //Set canvas to match image
        function setImDim(image) {
            canvas.width = image.width;
            canvas.height = image.height;
        }

        //Draw the image on the canvas
        function drawImage(image) {
            ctx.drawImage(image, 0, 0);
        }

        //Handle user drawing on the canvas
        //Move down
        function mouseDown(e) {
            canvas.addEventListener('mousemove', mouseMove);
            canvas.addEventListener('mouseup', mouseUp);

            ctx.beginPath();
            ctx.moveTo(e.offsetX, e.offsetY);

            ctx.strokeStyle = document.getElementById('colorPicker').value;
        }

        //Move general
        function mouseMove(e) {
            ctx.lineTo(e.offsetX, e.offsetY);
            ctx.stroke();
        }

        //Move up
        function mouseUp() {
            canvas.removeEventListener('mousemove', mouseMove);
            canvas.removeEventListener('mouseup', mouseUp);
        }

        //Clear canvas
        function clearCanvas() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            drawImage(image);
        }

        //Handle user selecting an image
        document.getElementById('imageFile').addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function(e) {
                    const image = new Image();
                    image.src = e.target.result;
                    image.onload = function() {
                        setImDim(image);
                        drawImage(image);
                    }
                }
            }
        });

        function saveImage(canvas) {
            //Converts edit to PNG format
            var dataURL = canvas.toDataURL("image/png");

            //Create download link
            var link = document.createElement("a");
            link.download = "edited-image.png";
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

