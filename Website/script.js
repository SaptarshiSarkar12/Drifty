"use strict";

/* Javascript alert */
const gtbStars = document.querySelector(".github-stars-count");
const gtbForks = document.querySelector(".github-forks-count");
fetch(
  "https://api.github.com/search/repositories?q=user:SaptarshiSarkar12+repo:Drifty+Drifty"
)
  .then((res) => res.json())
  .then((res) => {
    gtbStars.innerHTML = `Stars: ${res?.items[0]?.stargazers_count}`;
    gtbForks.innerHTML = `Forks: ${res?.items[0]?.forks_count}`;
  });

function download_alert_window() {
  let w = confirm("Do you want to download Drifty?");
  if (w === true) {
    alert("Thanks for Downloading");
    window.open(
      "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty_CLI.exe"
    );
  } else {
    alert("Sorry! You cancelled the Download!");
  }
}

function download_alert_apple() {
  let a = confirm("Do you want to download Drifty?");
  if (a === true) {
    alert("Thanks for Downloading");
    window.open(
      "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty.jar"
    );
  } else {
    alert("Sorry! You cancelled the Download!");
  }
}

const date = new Date();

let darkMode = localStorage.getItem("darkMode");
const darkModeToggle = document.querySelector(".btn-toggle");

const nav = document.querySelector(".nav-bar");
const menu = document.getElementById("menu");
const toggleBtn = document.querySelector("#menu>i");
const miniNav = document.querySelector("nav");
const links = document.querySelectorAll(".nav-links > li");
const darkTxt = document.querySelector(".Txt");
const symb = document.getElementById("symb");

const enableDarkMode = () => {
  document.body.classList.add("darkmode");
  darkModeToggle.checked = true; // Check only if the darkmode is on
  symb.classList.remove("fa-moon");
  symb.classList.add("fa-sun");
  localStorage.setItem("darkMode", "enabled");
};

const disableDarkMode = () => {
  document.body.classList.remove("darkmode");
  darkModeToggle.checked = false;
  symb.classList.add("fa-moon");
  symb.classList.remove("fa-sun");
  localStorage.setItem("darkMode", null);
};

if (darkMode === "enabled") enableDarkMode();

darkModeToggle.addEventListener("click", () => {
  darkMode = localStorage.getItem("darkMode");
  // darkTxt.innerHTML= "Dark"

  if (darkMode !== "enabled") enableDarkMode();
  else disableDarkMode();
});

// NAV ---------

window.addEventListener("scroll", function () {
  nav.classList.toggle("nav-sticky", this.window.scrollY > 10);
});

menu.addEventListener("click", function () {
  miniNav.classList.toggle("show");
  if (!nav.classList.contains("nav-sticky")) {
    nav.classList.toggle("temp-color");
  } else {
    if (nav.classList.contains("temp-color")) {
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
let contrib = localStorage.getItem("contrib");
let requestedAt = localStorage.getItem("requestedAt");
if (parseInt(requestedAt) > 23) localStorage.removeItem("requestedAt");
if (contrib == null || requestedAt == null) getContrib();
else if (currentHour != requestedAt) getContrib();
else updateContrib();

function getContrib() {
  fetch("https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors")
    .then((x) => x.json())
    .then((data) => {
      contrib = data;
      localStorage.setItem("contrib", JSON.stringify(data));
      localStorage.setItem("requestedAt", date.getHours());
      updateContrib();
    })
    .catch((err) => console.log(err));
}

function updateContrib() {
  try {
    contrib = JSON.parse(contrib);
  } catch (_) {
    console.log(_);
  }
  contrib.forEach((x) =>
    document.getElementById("contributors").appendChild(generateDiv(x))
  );
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
  div.style.margin = "5px";
  return div;
}
// END CONTRIB ----

// Release SECTION ----
let releases = localStorage.getItem("releases");
let releasesRequestedAt = localStorage.getItem("releasesRequestedAt");
if (parseInt(releasesRequestedAt) > 23) localStorage.removeItem("requestedAt");
if (releases == null || releasesRequestedAt == null) getReleases();
else if (currentHour - releasesRequestedAt >= 4)
  getReleases(); // get release info every 4 hours
else updateReleases();

// latest release could be defined as release with higher id;
function setLatest(releases) {
  let maxiId = Math.max(...releases.map((re) => re.id));
  releases = releases.map((release) => {
    release.isLatest = release.id == maxiId;
    return release;
  });
  return releases;
}

function getReleases() {
  fetch("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases")
    .then((x) => x.json())
    .then((data) => {
      releases = data;
      releases = setLatest(releases);
      localStorage.setItem("releases", JSON.stringify(data));
      localStorage.setItem("releasesRequestedAt", date.getHours());
      updateReleases();
    })
    .catch((err) => console.log(err));
}

function updateReleases() {
  try {
    releases = JSON.parse(releases);
  } catch (_) {
    console.log(_);
  }
  document.getElementById("releases").innerHTML = renderReleases(releases);
}

function renderReleases(releases) {
  return releases.reduce(renderRelease, "");
}

function renderRelease(all, release) {
  release.totalCount = release.assets.reduce(
    (total, rel) => total + rel.download_count,
    0
  );
  let assets = renderAssets(release.assets.reverse());
  return `${all} <div class="release">
  <div style="text-align:center"><b>${release.name}</b> ${
    release.isLatest ? "<b class='label'>Latest</b>" : ""
  }</div>
  <div style="text-align:center">${new Date(release.published_at)} with <b>${
    release.totalCount
  } </b> Downloads</div>
  <div onclick="toggleMore(this,'${
    release.id
  }')" style="text-align:center;cursor:pointer;font-weight:bolder;opacity: 0.4;">Learn More</div>
  <div id="${release.id}" class="release-note">
    ${marked.parse(release.body)}
  </div>
      <div class="assets">
             ${assets}
      </div>
  </div>`;
}

function toggleMore(btn, id) {
  let release_note = document.getElementById(id);
  // calculate the real height of the element which is scrollHeight + margin(1rem top and 1 rem bottom) + border(0)
  // let height = release_note.scrollHeight + parseFloat( getComputedStyle(document.documentElement).fontSize.split("px")[0] ) *2;
  // or simply we could multiply the scroll height by two and the height could not be more that this
  let height = release_note.scrollHeight * 2;

  if (btn.innerText == "Hide") {
    release_note.style.maxHeight = "0px";
    release_note.style.margin = "0rem";
    btn.innerText = "Learn More";
  } else {
    release_note.style.maxHeight = height + "px";
    release_note.style.margin = "1rem";
    btn.innerText = "Hide";
  }
}

function renderAssets(assets) {
  return assets.reduce((all, asset) => {
    //check if it is for windows
    let is_windows = asset.name.split(".").pop().toLocaleLowerCase() === "exe";

    if (is_windows) {
      return `${all} <a href="${asset.browser_download_url}">Download <i class="fab fa-windows"></i></a>`;
    } else {
      return `${all} <a href="${asset.browser_download_url}">Download <i class="fab fa-apple"></i> <i class="fab fa-linux"></i></a>`;
    }
  }, "");
}

let mybutton = document.getElementById("mybtn");
window.onscroll = function () {
  scrollFunction();
};
function scrollFunction() {
  if (
    document.body.scrollTop > 650 ||
    document.documentElement.scrollTop > 650
  ) {
    mybutton.style.display = "block";
  } else {
    mybutton.style.display = "none";
  }
}
function totop() {
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}
// END RELEASE
