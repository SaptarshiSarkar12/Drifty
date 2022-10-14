"use strict";

const date = new Date();

let darkMode = localStorage.getItem('darkMode');
const darkModeToggle = document.querySelector('.btn-toggle');

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


   




// END NAV ---------
  // links.fo

// CONTRIB SECTION ---- 
let currentHour = date.getHours();
let contrib = localStorage.getItem('contrib');
let requestedAt = localStorage.getItem("requestedAt");
if (parseInt(requestedAt)>23) localStorage.removeItem("requestedAt");
if (contrib == null ||  requestedAt == null) getContrib();
else if (currentHour!=requestedAt) getContrib();
function getContrib() {
    fetch("https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors")
    .then(x=>x.json())
    .then((data) => {
      contrib = data;
      localStorage.setItem('contrib', JSON.stringify(data));
      localStorage.setItem('requestedAt', date.getHours());
  }).catch(err => console.log(err));
}

updateContrib();
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



// Release SECTION ----
let releases = localStorage.getItem("releases");
let releasesRequestedAt = localStorage.getItem("releasesRequestedAt");
if (parseInt(releasesRequestedAt) > 23) localStorage.removeItem("requestedAt"); // fetch release information every day
if (releases == null || releasesRequestedAt == null) getReleases();
else if (currentHour != releasesRequestedAt) getReleases();
else updateReleases();

function getReleases() {
  fetch("https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases")
    .then((x) => x.json())
    .then((data) => {
      releases = data;
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
  console.log(release);
  let assets = renderAssets(release.assets.reverse());
  return `${all} <div class="release">
  <div><b>${release.name}</b></div>
  <div>${new Date(release.published_at)}</div>
      <div class="assets">
             ${assets}
      </div>
  </div>`;
}

function renderAssets(assets) {
  return assets.reduce((all, asset) => {
    //check if it is for windows
    let is_windows = asset.name.split(".").pop().toLocaleLowerCase() == "exe";

    if (is_windows) {
      return `${all} <a href="${asset.browser_download_url}">Download <i class="fab fa-windows"></i></a>`;
    } else {
      return `${all} <a href="${asset.browser_download_url}">Download <i class="fab fa-apple"></i> <i class="fab fa-linux"></i></a>`;
    }
  }, "");
}
// END RELEASE
