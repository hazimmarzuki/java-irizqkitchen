
  // google search bar in the header
// const f = document.getElementById('form');
// const q = document.getElementById('query');
// const google = 'https://www.google.com/search?q=site%3A+';


// function submitted(event) {
//   event.preventDefault();
//   const url = google + q.value;
//   const win = window.open(url, '_blank');
//   win.focus();
// }

// function Validate() {
//   var name = document.getElementById("username");
//   var email = document.getElementById("email");
//   var phone = document.getElementById("phone");
//   var address = document.getElementById("address");

//   var mailformat =
//     /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
//   var phoneformat = /^\(?(\d{3})\)?[- ]?(\d{3})[- ]?(\d{4})$/;

//   if (name.value.length <= 0) {
//     alert("Name must be filled out");
//     return false;
//   }
//   else if (email.value.length > 0) {
//     if (mailformat.test(email.value)) {
//       if (phone.value.length <= 0) {
//         alert("Phone must be filled out");
//         return false;
//       } else {
//         if (phoneformat.test(phone.value)) {
//           if(address.value.length > 0){
//             alert("Order has been submit");
//             return true;
//           }
//           else {
//             alert("Address must be filled out");
//             return false;
//           }
//         } else {
//           alert("Invalid phone number!");
//         }
//       }
//     } else {
//       alert("Invalid email address!");
//     }
//   } else {
//     alert("Email must be filled out");
//     return false;
//   }
// }

// function reset(){
//   document.getElementsByTagName("form").reset();
// }

const panels = document.querySelectorAll('.panel');



panels.forEach((panel) => {
panel.addEventListener('click', () => {
removeActiveClasses();
panel.classList.add('active');
})
})


function removeActiveClasses() {
panels.forEach(panel => {
panel.classList.remove('active');
})
}