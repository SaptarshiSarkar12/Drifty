import "./globals.css";
import Navbar from "../components/Navbar";
import { Metadata } from "next";
import Footer from "../components/Footer";

export const metadata: Metadata = {
  title: "Drifty",
  description: "Drifty - Open source Project",
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
        </main>
        <Footer />
      </body>
    </html>
  );
}
