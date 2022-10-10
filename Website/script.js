"use strict";

const date = new Date();

let darkMode = localStorage.getItem('darkMode');
const darkModeToggle = document.querySelector('.btn-toggle');

const nav = document.querySelector(".nav-bar");
const menu = document.getElementById("menu");
const toggleBtn = document.querySelector("#menu>i");
const miniNav = document.querySelector("nav");
const links = document.querySelectorAll(".nav-links > li");
const darkTxt = document.querySelector(".Txt");
const symb = document.getElementById("symb");

const enableDarkMode = () => {
  document.body.classList.add('darkmode');
  darkModeToggle.checked = true; // Check only if the darkmode is on
  symb.classList.remove("fa-moon");
  symb.classList.add("fa-sun");
  localStorage.setItem('darkMode', 'enabled');
}

const disableDarkMode = () => {
  document.body.classList.remove('darkmode');
  darkModeToggle.checked = false;  
  symb.classList.add("fa-moon");
  symb.classList.remove("fa-sun");  
  localStorage.setItem('darkMode', null);
}

if (darkMode === 'enabled')
  enableDarkMode();

darkModeToggle.addEventListener('click', () => {
  darkMode = localStorage.getItem('darkMode');
  // darkTxt.innerHTML= "Dark"

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

// CONTRIB SECTION ---- 
let currentHour = date.getHours();
let contrib = localStorage.getItem('contrib');
let requestedAt = localStorage.getItem("requestedAt");
if (parseInt(requestedAt)>23) localStorage.removeItem("requestedAt");
if (contrib == null ||  requestedAt == null) getContrib();
else if (currentHour!=requestedAt) getContrib();
else updateContrib();

function getContrib() {
    fetch("https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors")
    .then(x=>x.json())
    .then((data) => {
      contrib = data;
      localStorage.setItem('contrib', JSON.stringify(data));
      localStorage.setItem('requestedAt', date.getHours());
      updateContrib();
  }).catch(err => console.log(err));
}

function updateContrib(){
  try{
    contrib = JSON.parse(contrib);
  } catch (_) {
  console.log(_);
  }
  contrib.forEach(x=> document
    .getElementById("contributors")
    .appendChild(generateDiv(x))); 
}

// x - user_object
// returns - div with img
function generateDiv(x) {
  let img = document.createElement("img");
  img.src = `${x["avatar_url"]}`;
  img.style.height = "48px";
  img.style.width = "48px";
  let a = document.createElement("a");
  a.href = `${x["html_url"]}`;
  a.appendChild(img);
  
  let div = document.createElement("div");
  div.appendChild(a);
    div.style.width = "48px";
    div.style.height = "48px";
    div.style.margin = "5px"
    return div;
}
// END CONTRIB ---- 