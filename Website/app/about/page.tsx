import { Metadata } from "next";
import AboutPage from "../../components/AboutPage";

export const metadata: Metadata = {
  title: "About | Drifty",
};

export default function Page() {
  return <AboutPage />;
}
