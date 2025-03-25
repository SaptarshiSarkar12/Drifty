import { Layout, Navbar } from "nextra-theme-docs";
import { Banner } from "nextra/components";
import { getPageMap } from "nextra/page-map";
import "nextra-theme-docs/style.css";

export const metadata = {
  // Define your metadata here
  // For more information on metadata API, see: https://nextjs.org/docs/app/building-your-application/optimizing/metadata
  title: "Drifty Docs",
  description: "Documentation page for Drifty",
};

let etag = null;

const version = await (async () => {
  try {
    const headers = {
      Accept: "application/vnd.github.v3+json",
    };

    if (etag) headers["If-None-Match"] = etag; // Use ETag for caching

    const res = await fetch(
      "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest",
      { headers },
    );

    if (res.status === 304) {
      console.log("GitHub API: Not Modified (using cached version)");
      return null; // No changes, use previous version
    }

    if (!res.ok) throw new Error("GitHub API error");

    etag = res.headers.get("etag"); // Store the new ETag
    const data = await res.json();
    return data.tag_name || null;
  } catch (error) {
    console.error("Error fetching version:", error);
    return null;
  }
})();

const banner = (
  <Banner storageKey="drifty-banner-storage-01">
    {version ? (
      <>
        🚀 <strong>Drifty {version} is live!</strong> Explore the latest
        features, join the{" "}
        <a
          href="https://discord.gg/DeT4jXPfkG"
          className="underline underline-offset-2"
        >
          community
        </a>{" "}
        or report an{" "}
        <a
          href="https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose"
          className="underline underline-offset-2"
        >
          issue
        </a>
        !
      </>
    ) : (
      <>
        👋 <strong>Welcome to Drifty!</strong> Stay tuned for updates. Need
        help? Join the{" "}
        <a
          href="https://discord.gg/DeT4jXPfkG"
          className="underline underline-offset-2"
        >
          community
        </a>{" "}
        or report an{" "}
        <a
          href="https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose"
          className="underline underline-offset-2"
        >
          issue
        </a>
        !
      </>
    )}
  </Banner>
);
const navbar = (
  <Navbar
    logo={
      <span style={{ marginLeft: ".4em", fontWeight: 600 }}>Drifty Docs</span>
    }
  />
);

export default async function DocsLayout({ children }) {
  return (
    <>
      <Layout
        banner={banner}
        pageMap={await getPageMap()}
        navbar={navbar}
        docsRepositoryBase="https://github.com/SaptarshiSarkar12/Drifty"
        darkMode={false}
        sidebar={{
          toggleButton: false,
          autoCollapse: true,
          defaultMenuCollapseLevel: 1,
        }}
        feedback={{
          content: "Something Wrong? Help Us Improve!",
        }}
        editLink={null}
      >
        {children}
      </Layout>
    </>
  );
}
