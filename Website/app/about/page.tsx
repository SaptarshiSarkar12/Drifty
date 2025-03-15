import AboutPage from "@/components/AboutPage";
import { Metadata } from "next";

export const metadata: Metadata = {
  title: "About | Drifty",
};

export default function Page() {
  return <AboutPage />;
}
