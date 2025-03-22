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

const banner = (
  <Banner storageKey="drifty-banner-key-01">
    Welcome to Drifty Documentation!
  </Banner>
);

const navbar = (
  <Navbar
    logo={<span></span>}
    className="opacity-100 translate-y-0 visible"
  ></Navbar>
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
