"use client";
import { useState, useEffect } from "react";
import { usePathname } from "next/navigation";
import Link from "next/link";
import { IoMenu, IoClose } from "react-icons/io5";
import Image from "next/image";

export default function Navbar() {
    const [isOpen, setIsOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);
    const pathname = usePathname();

    useEffect(() => {
        const handleScroll = () => {
          setScrolled(window.scrollY > 0);
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        setIsOpen(false);
    }, [pathname]);

    const navItems = {
        "/": "Home",
        "/about": "About",
        "/contact": "Contact",
        "/download": "Download",
        "/docs": "Docs",
    };

    return (
        <nav
          className={`fixed top-0 left-0 w-full p-4 z-50 transition-all duration-300 ${
            scrolled
              ? "shadow-md dark:bg-(--dark-accent) bg-(--light-accent)"
              : "bg-transparent shadow-none"
          }`}
        >
          <div className="container mx-auto flex justify-between items-center">
            {/* Logo Section */}
            <div className="flex items-center">
              <Link href="/">
                <span className="text-lg font-bold ">
                  <Image
                    src="/DRIFTY.svg"
                    width={100}
                    height={100}
                    alt="Logo"
                    className="w-30 md:w-34 lg:w-40 xl:w-46 h-auto"
                  />
                </span>
              </Link>
            </div>

            {/* Right-side Nav Menu and Search */}
            <div className="flex items-center ml-auto space-x-6">
              {/* Desktop Nav Items */}
              <ul className={`hidden md:flex md:items-center md:space-x-6`}>
                {Object.entries(navItems).map(([path, label]) => (
                  <li
                    key={path}
                    className={`p-2 md:p-0 ${
                      pathname === path ? "font-bold underline " : ""
                    }`}
                  >
                    <Link href={path} className="block md:inline-block">
                      {label}
                    </Link>
                  </li>
                ))}
              </ul>

              {/* Mobile Menu Toggle Button */}
              <div className="md:hidden">
                <button onClick={() => setIsOpen(!isOpen)} className="">
                  {isOpen ? <IoClose size={24} /> : <IoMenu size={24} />}
                </button>
              </div>
            </div>

            {/* Mobile Nav Menu */}
            <ul
              className={`md:hidden absolute top-16 left-0 w-full md:w-auto transition-all duration-300 ease-in-out transform ${
                isOpen
                  ? "opacity-100 translate-y-0 visible dark:bg-(--dark-accent) bg-(--light-accent)"
                  : "opacity-0 -translate-y-5 invisible md:visible md:opacity-100 md:translate-y-0 bg-transparent"
              }`}
            >
              {Object.entries(navItems).map(([path, label]) => (
                <li
                  key={path}
                  className={`p-2 md:p-0 ${
                    pathname === path ? "font-bold underline " : ""
                  }`}
                >
                  <Link href={path} className="block md:inline-block">
                    {label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </nav>
    );
}
