import nextra from "nextra";

const withNextra = nextra({
  search: true,
  contentDirBasePath: "/docs",
  defaultShowCopyCode: true,
  codeHighlight: true,
});

// You can include other Next.js configuration options here, in addition to Nextra settings:
export default withNextra({
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "avatars.githubusercontent.com" },
    ],
  },
  reactStrictMode: true,
});
