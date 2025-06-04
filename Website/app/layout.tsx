import "./globals.css";
import Navbar from "../components/Navbar";
import { Metadata } from "next";
import { Analytics } from "@vercel/analytics/react";
import { SpeedInsights } from "@vercel/speed-insights/next";
import Footer from "../components/Footer";

export const metadata: Metadata = {
  title: "Drifty",
  description: "Drifty - Open-Source Interactive File Downloader",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className="flex flex-col min-h-screen dark:bg-(--bg-color) dark:text-(--text-color)">
        <Navbar />
        <main className="container mx-auto p-4 pt-16 flex-grow">
          {children}
          <Analytics />
          <SpeedInsights />
        </main>
        <Footer />
      </body>
    </html>
  );
}
