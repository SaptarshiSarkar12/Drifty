import Releases from "../Releases";
import Header from "../Header";
import Footer from "../Footer";

export const metadata = {
  title: "Download",
  description: "Download Drifty"
};

export default async function download() {
  const data = await getData();
  return (
    <>
      <Header props={"bg-top"} />
      <Releases className="bg-about" props={data} />
      <Footer />
    </>
  );
}

export async function getData() {
  const res = await fetch(
    "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases",
    { method: "GET" }
  );
  const release = await res.json();
  return {
    release,
    revalidate: 3600,
  };
}
