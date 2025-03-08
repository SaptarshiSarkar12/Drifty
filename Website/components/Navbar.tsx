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

  const navItems = {
    "/": "Home",
    "/about": "About",
    "/contact": "Contact",
    "/download": "Download",
  };

  return (
    <nav
      className={`fixed top-0 left-0 w-full p-4 z-99 transition-all duration-300 ${
        scrolled
          ? "shadow-md dark:bg-(--dark-accent) bg-(--light-accent)"
          : "bg-transparent shadow-none"
      }`}
    >
      <div className="container mx-auto flex flex-row justify-between items-center">
        <div className="flex items-center">
          <Link href="/">
            <span className="text-lg font-bold ">
              <Image
                src="/DRIFTY.svg"
                width={100}
                height={100}
                alt="Logo"
                className="w-18 sm:w-26 md:w-34 lg:w-40 xl:w-46 h-auto"
              />
            </span>
          </Link>
        </div>
        <ul
          className={`md:flex md:items-center md:space-x-6 absolute md:static top-16 left-0 w-full md:w-auto ${
            scrolled
              ? "dark:bg-(--dark-accent) bg-(--light-accent)"
              : "bg-transparent"
          } md:bg-transparent md:dark:bg-transparent transition-transform duration-300 ease-in-out ${
            isOpen ? "block" : "hidden"
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
        <div className="md:hidden ml-auto">
          <button onClick={() => setIsOpen(!isOpen)} className="">
            {isOpen ? <IoClose size={24} /> : <IoMenu size={24} />}
          </button>
        </div>
      </div>
    </nav>
  );
}
