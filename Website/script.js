// const options = {
//   bottom: "32px",
//   right: "32px",
//   left: "unset",
//   time: "0.5s",
//   mixColor: "#fff",
//   backgroundColor: "#fff",
//   buttonColorDark: "#100f2c",
//   buttonColorLight: "#fff",
//   saveInCookies: false,
//   label: "🌓",
//   autoMatchOsTheme: true,
// };
//
// const darkmode = new Darkmode(options);
// darkmode.showWidget();
// const swiper = new Swiper(".swiper", {
//   loop: true,
//   pagination: {
//     el: ".swiper-pagination",
//   },

//   navigation: {
//     nextEl: ".swiper-button-next",
//     prevEl: ".swiper-button-prev",
//   },
// });
let darkMode = localStorage.getItem('darkMode');
const darkModeToggle = document.querySelector('.btn-toggle');

const enableDarkMode = () => {
  document.body.classList.add('darkmode');
  localStorage.setItem('darkMode', 'enabled');
}

const disableDarkMode = () => {
  document.body.classList.remove('darkmode');
  localStorage.setItem('darkMode', null);
}

if (darkMode === 'enabled')
  enableDarkMode();

darkModeToggle.addEventListener('click', () => {
  darkMode = localStorage.getItem('darkMode');
  if (darkMode !== 'enabled')
    enableDarkMode();
  else
    disableDarkMode();
});

