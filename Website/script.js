let darkMode = localStorage.getItem('darkMode');
const darkModeToggle = document.querySelector('.btn-toggle');

const nav = document.querySelector(".nav-bar");
const menu = document.getElementById("menu");
const toggleBtn = document.querySelector("#menu>i");
const miniNav = document.querySelector("nav");

const links = document.querySelectorAll(".nav-links > li");

const enableDarkMode = () => {
  document.body.classList.add('darkmode');
  darkModeToggle.checked = true; // Check only if the darkmode is on
  localStorage.setItem('darkMode', 'enabled');
}

const disableDarkMode = () => {
  document.body.classList.remove('darkmode');
  darkModeToggle.checked = false;
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

// NAV ---------

window.addEventListener("scroll", function () {
  nav.classList.toggle("nav-sticky", this.window.scrollY > 10);
  });
  
  menu.addEventListener("click", function () {
  miniNav.classList.toggle("show");
  if (!nav.classList.contains("nav-sticky")){
    nav.classList.toggle("temp-color");
  } else {
    if (nav.classList.contains("temp-color")){
      nav.classList.remove("temp-color");
    }
  }
  links.forEach((x) => x.classList.toggle("fade"));
  toggleNavIcon();
  });
  
  function toggleNavIcon() {
  if (toggleBtn.classList.contains("fa-bars")) {
  toggleBtn.classList.remove("fa-bars");
  toggleBtn.classList.add("fa-x");
  return;
  }
  toggleBtn.classList.remove("fa-x");
  toggleBtn.classList.add("fa-bars");
  }
  
  // END NAV ---------

