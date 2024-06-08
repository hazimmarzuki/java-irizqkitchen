function uploadFile() {
    var fileInput = document.getElementById("fileUpload");
    var file = fileInput.files[0];
    
    if (file) {
      var formData = new FormData();
      formData.append("fileUpload", file);

      // Send formData using AJAX or fetch
        // Example using fetch and POST method
        fetch("/upload", {
            method: "POST",
            body: formData
          })
          .then(response => {
            // Handle the response from the server
            console.log("File uploaded successfully!");
          })
          .catch(error => {
            // Handle any error that occurred during the file upload
            console.error("File upload error:", error);
          });
        }
    }