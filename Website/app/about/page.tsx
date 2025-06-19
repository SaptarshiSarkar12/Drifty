import AboutPage from "@/components/AboutPage";
import { Metadata } from "next";

export const metadata: Metadata = {
  title: "About",
};

export default function Page() {
  return <AboutPage />;
}
