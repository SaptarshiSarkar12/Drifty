"use client";

import Image from "next/image";
import { useState } from "react";
import Link from "next/link";
import { useCallback, useEffect } from "react";

function NavLink({ to, children, cn, setOpen }) {
  return (
    <Link
      scroll={false}
      href={to}
      className={`text-gray-900 ${cn}`}
      onClick={() => {
        setOpen(false);
      }}
    >
      {children}
    </Link>
  );
}

function MobileNav({ open, setOpen }) {
  return (
    <div
      className={`fixed z-10 top-0 left-0 h-screen w-screen flex flex-col gap-12 pt-16 bg-top transform ${
        !open && "-translate-x-full"
      } transition-transform duration-300 ease-in-out filter drop-shadow-md`}
    >
      {/* Logo container */}
      <div className="flex items-start justify-center filter bg-top h-30 m-4 z-50">
        <Link className="text-xl font-semibold z-50" href="/">
          <Image
            src="Drifty1024Thinner1Px.png"
            width={80}
            height={80}
            alt="Logo of Drifty"
          />
        </Link>
      </div>
      {/* Mobile Nav */}
      <div className="flex flex-col gap-12 justify-items-center items-center z-50">
        <NavLink
          to="/about"
          cn="flex py-2.5 px-3.5 text-white rounded-lg hover:bg-[#004f6a7d] duration-500 font-semibold text-xl"
          setOpen={setOpen}
        >
          <button>About</button>
        </NavLink>
        <NavLink
          to="/download"
          cn="flex py-2.5 px-3.5 text-white rounded-lg hover:bg-[#004f6a7d] duration-500 font-semibold text-xl"
          setOpen={setOpen}
        >
          <button>Download</button>
        </NavLink>
        <NavLink
          to="/contact"
          cn="flex py-2.5 px-3.5 text-white rounded-lg hover:bg-[#004f6a7d] duration-500 font-semibold text-xl"
          setOpen={setOpen}
        >
          <button>Contact</button>
        </NavLink>
        {/* Social Icons */}
        <div className="flex justify-center items-center pt-10 z-50">
          <a href="https://discord.gg/DeT4jXPfkG" target="_blank">
            <i
              className={
                "fab fa-discord text-3xl text-white mx-4 hover:transition hover:ease-in-out delay-75 duration-300 hover:text-violet-600 z-50"
              }
            ></i>
          </a>

          <a href="https://github.com/SaptarshiSarkar12/Drifty" target="_blank">
            <i
              className={
                "fab fa-github text-3xl text-white mx-4 hover:transition hover:ease-in-out delay-75 duration-300 hover:text-black z-50"
              }
            ></i>
          </a>
        </div>
      </div>
    </div>
  );
}

export default function Header({ props }) {
  const [open, setOpen] = useState(false);
  const [hcolor, setHcolor] = useState(props + " pt-7");
  const onScroll = useCallback(() => {
    const { scrollY } = window;
    if (window.innerWidth <= 760 && open) {
      setOpen(false);
    }
    if (window.innerWidth > 760) {
      if (scrollY === 0) setHcolor(props + " pt-7");
      else setHcolor("bg-var shadow-lg pt-4");
    }
  }, [props, open]);

  useEffect(() => {
    // add event listener to window
    window.addEventListener("scroll", onScroll, { passive: true });
    // remove event listener on unmounting to prevent memory leaks
    return () => {
      window.removeEventListener("scroll", onScroll);
    };
  }, [onScroll]);

  return (
    <header
      className={`md:sticky top-0 ${hcolor} z-40 select-none md:overflow-hidden`}
    >
      <nav className="flex filter drop-shadow-md px-4 py-4 h-20 items-center ">
        <MobileNav open={open} setOpen={setOpen} />
        <div className="flex items-center lg:pl-20 mb-3 w-full">
          <Link className="font-semibold" href="/">
            <Image
              className="w-12 lg:w-16"
              src="Drifty1024Thinner1Px.png"
              alt="Logo Of Drifty"
              width={300}
              height={300}
            />
          </Link>
        </div>

        <div
          className="z-40 flex relative w-8 h-8 flex-col justify-between items-center md:hidden"
          onClick={() => {
            setOpen(!open);
          }}
          role={"button"}
        >
          {/* hamburger button */}
          <span
            className={`h-1 w-full bg-white rounded-lg transform transition duration-300 ease-in-out ${
              open ? "rotate-45 translate-y-3.5" : ""
            }`}
          />
          <span
            className={`h-1 w-full bg-white rounded-lg transition-all duration-300 ease-in-out ${
              open ? "hidden" : "w-full"
            }`}
          />
          <span
            className={`h-1 w-full bg-white rounded-lg transform transition duration-300 ease-in-out ${
              open ? "-rotate-45 -translate-y-3.5" : ""
            }`}
          />
        </div>

        {/* Desktop Navbar buttons */}
        <div className="hidden md:flex gap-4 lg:gap-8 justify-center items-center pr-4 lg:pr-8">
          <NavLink to="/#" cn="flex" setOpen={setOpen}>
            <button className="py-2 px-3.5 text-white hover:bg-[#004f6a7d] hover:transition rounded-lg font-bold tracking-wider text-base lg:text-lg duration-300">
              HOME
            </button>
          </NavLink>
          <NavLink to="/about" cn="flex" setOpen={setOpen}>
            <button className="py-2 px-3.5 text-white hover:bg-[#004f6a7d] hover:transition rounded-lg font-bold tracking-wider text-base lg:text-lg duration-300">
              ABOUT
            </button>
          </NavLink>
          <NavLink to="/download" cn="flex" setOpen={setOpen}>
            <button className="py-2 px-3.5 text-white hover:bg-[#004f6a7d] hover:transition rounded-lg font-bold tracking-wider text-base lg:text-lg duration-300">
              DOWNLOAD
            </button>
          </NavLink>
          <NavLink to="/contact" cn="flex" setOpen={setOpen}>
            <button className="py-2 px-3.5 text-white hover:bg-[#004f6a7d] hover:transition rounded-lg font-bold tracking-wider text-base lg:text-lg duration-300">
              CONTACT
            </button>
          </NavLink>
          <a href="https://discord.gg/DeT4jXPfkG" target="_blank">
            <i
              className={
                "fab fa-discord text-2xl text-white mx-4 duration-100 hover:text-violet-700"
              }
            ></i>
          </a>

          <a href="https://github.com/SaptarshiSarkar12/Drifty" target="_blank">
            <i
              className={
                "fab fa-github text-2xl text-white mx-4 duration-100 hover:text-black"
              }
            ></i>
          </a>
        </div>
      </nav>
    </header>
  );
}
