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

