import { useRouter } from "next/router";
import Image from "next/image";

export default {
  logo: (
    <>
      <Image src="/Drifty.svg" alt="logo" width="30" height="30"></Image>
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
    defaultMenuCollapseLevel: 1,
  },
  navigation: true,
  search: {
    placeholder: "Search the docs",
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
};
