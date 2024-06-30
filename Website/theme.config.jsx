<<<<<<< HEAD
<<<<<<< HEAD
import { useRouter } from "next/router";

export default {
  logo: (
    <>
      <img src="/Drifty.svg" alt="logo" width="30" height="30"></img>
      &nbsp;&nbsp;
      <span>
        <strong>Drifty Docs</strong>
      </span>
    </>
  ),
  project: {
    link: "https://github.com/SaptarshiSarkar12/Drifty",
  },
  chat: {
    link: "https://discord.gg/DeT4jXPfkG",
  },
  docsRepositoryBase:
    "https://github.com/SaptarshiSarkar12/Drifty/tree/master/Website",
  banner: {
    dismissible: true,
    text: "Welcome to Drifty Docs!",
  },

  useNextSeoProps() {
    const { asPath } = useRouter();
    if (asPath !== "/docs") {
      return {
        titleTemplate: "Drifty Docs | %s",
      };
    }
  },
  toc: {
    backToTop: true,
    float: true,
  },
  sidebar: {
    toggleButton: true,
    autoCollapse: true,
  },
  navigation: true,
  search: {
    placeholder: "Search ",
  },
  footer: {
    text: (
      <span>
        © {new Date().getFullYear()}{" "}
        <a href="/" target="_blank">
          Drifty
        </a>
        . All Rights Reserved.
      </span>
    ),
  },
=======
=======
import { useRouter } from "next/router";

>>>>>>> 821c12a (Add  navigation and Discord links and Logo)
export default {
  logo: (
    <>
      <img src="/Drifty.svg" alt="logo" width="30" height="30"></img>
      &nbsp;&nbsp;
      <span>
        <strong>Drifty Docs</strong>
      </span>
    </>
  ),
  project: {
    link: "https://github.com/SaptarshiSarkar12/Drifty",
  },
<<<<<<< HEAD
<<<<<<< HEAD
  // ... other theme options
>>>>>>> 6b502dc (init docs)
=======
=======
  chat: {
    link: "https://discord.gg/DeT4jXPfkG",
  },
>>>>>>> 821c12a (Add  navigation and Discord links and Logo)
  docsRepositoryBase:
    "https://github.com/SaptarshiSarkar12/Drifty/tree/master/Website",
  banner: {
    dismissible: true,
    text: "Welcome to Drifty Docs!",
  },

  useNextSeoProps() {
    const { asPath } = useRouter();
    if (asPath !== "/docs") {
      return {
        titleTemplate: "Drifty Docs | %s",
      };
    }
  },
  toc: {
    backToTop: true,
    float: true,
  },
  sidebar: {
    toggleButton: true,
    autoCollapse: true,
  },
  navigation: true,
  search: {
    placeholder: "Search ",
  },
  footer: {
    text: (
      <span>
        © {new Date().getFullYear()}{" "}
        <a href="/" target="_blank">
          Drifty
        </a>
        . All Rights Reserved.
      </span>
    ),
  },
>>>>>>> 62b3acc (Update Broken Links to Github)
};
