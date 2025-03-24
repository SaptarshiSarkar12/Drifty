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

const version = await (async () => {
  try {
    const res = await fetch(
      "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest"
    );
    const data = await res.json();
    return data.tag_name || "Unknown";
  } catch {
    return "Unknown";
  }
})();

const banner = (
  <Banner storageKey="drifty-banner-key-01">
    👋 Welcome to Drifty Docs! Now supporting {version} -- need help? Ask the{" "}
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
  </Banner>
);

export default async function DocsLayout({ children }) {
  return (
    <>
      <Layout
        banner={banner}
        pageMap={await getPageMap()}
        docsRepositoryBase="https://github.com/SaptarshiSarkar12/Drifty"
        darkMode={false}
        sidebar={{
          toggleButton: false,
          autoCollapse: true,
          defaultMenuCollapseLevel: 1,
        }}
      >
        {children}
      </Layout>
    </>
  );
}
