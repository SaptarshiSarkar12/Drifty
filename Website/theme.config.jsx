export default {
  logo: <span>Drifty Docs</span>,
  project: {
    link: "https://github.com/SaptarshiSarkar12/Drifty",
  },
  docsRepositoryBase:
    "https://github.com/SaptarshiSarkar12/Drifty/tree/master/docs",
  useNextSeoProps() {
    return {
      titleTemplate: "%s | Drifty",
    };
  },
  toc: {
    backToTop: true,
    float: true,
  },
  sidebar: { toggleButton: true, autoCollapse: true },
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
};
