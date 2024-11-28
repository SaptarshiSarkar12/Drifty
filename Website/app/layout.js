import "./globals.css";
import { Inter } from "next/font/google";

const inter = Inter({ subsets: ["latin"] });
export const revalidate = 3600;

export const metadata = {
  title: {
    template: "Drifty | %s",
    default: "Drifty",
  },
  description: "An Open-Source Interactive File Downloader System",
};

export const viewport = {
  width: "device-width",
  initialScale: 1,
  themeColor: [
    { media: "(prefers-color-scheme: dark)", color: "#0000cd" },
    { media: "(prefers-color-scheme: light)", color: "#26a3f1" },
  ],
};

export default function RootLayout({ children }) {
  return (
    <html lang="en" className="scroll-smooth overflow-x-hidden">
      <head>
        <script
          src="https://kit.fontawesome.com/d93986da58.js"
          crossOrigin="anonymous"
          async
        ></script>
        <meta
          name="google-site-verification"
          content="v1CBGEYFNFLCTulyDwIJDu2cV0EA2ZwokOXQC9GfkiE"
        />
      </head>
      <body className={inter.className}>{children}</body>
    </html>
  );
}
