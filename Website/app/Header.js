"use client";
import Image from "next/image";
import { useState } from "react";
import Link from "next/link";
import { useCallback, useEffect } from "react";
function NavLink({ to, children, cn }) {
  return (
    <Link
      scroll={false}
      href={to}
      className={`text-gray-900 hover:text-black ${cn}`}
    >
      {children}
    </Link>
  );
}

function MobileNav({ open }) {
  return (
    <div
      className={`absolute z-10 top-0 left-0 h-screen w-screen bg-top transform ${
        !open && "-translate-x-full"
      } transition-transform duration-300 ease-in-out filter drop-shadow-md`}
    >
      {/* Mobile Nav */}
      <div className="flex flex-col pt-12 justify-items-center items-center">
        <NavLink
          to="/about"
          cn="text-2xl font-normal my-6 text-white hover:text-[#191B33] hover:scale-105 duration-500"
        >
          <button className="">About</button>
        </NavLink>
        <NavLink
          to="/download"
          cn="text-2xl font-normal my-6 text-white hover:text-[#191B33] hover:scale-105 duration-500"
        >
          <button className="">Download</button>
        </NavLink>
        <NavLink
          to="/contact"
          cn="text-2xl font-normal my-6 text-white hover:text-[#191B33] hover:scale-105 duration-500"
        >
          <button className="">Contact</button>
        </NavLink>
        <div className="flex justify-center items-center pt-10">
          <a href="https://discord.gg/kP4ecaR4" target="_blank">
            <img
              className="mx-8 hover:scale-110 duration-500"
              height={35}
              width={30}
              src="discord-logo.png"
              alt="discord logo"
            />
          </a>

          <a href="https://github.com/SaptarshiSarkar12/Drifty" target="_blank">
            <img
              className="mx-8 hover:scale-110 duration-500"
              height={30}
              width={35}
              src="github-logo.png"
              alt="github logo"
            />
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
    if (scrollY === 0) setHcolor(props + " pt-7");
    else setHcolor("bg-var shadow-lg pt-4");
  }, [props]);

  useEffect(() => {
    //add event listener to window
    window.addEventListener("scroll", onScroll, { passive: true });
    // remove event on unmounting to prevent Linkmemory leak with the cleanup
  });
  return (
    <header className={`md:sticky top-0 ${hcolor} z-40 select-none `}>
      <nav className="flex filter drop-shadow-md px-4 py-4 h-20 items-center">
        <MobileNav open={open} setOpen={setOpen} />
        <div className="flex items-center lg:pl-20 mb-3 w-full">
          <Link className="font-semibold" href="/">
            <Image
              className="w-16 xs:hidden md:block"
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

        {/* NAVBAR BUTTONS */}
        <div className="hidden md:flex mr-20 ">
          <NavLink to="/#" cn="mx-4 flex">
            <button className="text-white hover:text-[white] hover:scale-110 duration-500 font-bold">
              HOME
            </button>
          </NavLink>
          <NavLink to="/about" cn="mx-4 flex">
            <button className="text-white hover:text-[white] hover:scale-110 duration-500 font-bold">
              ABOUT
            </button>
          </NavLink>
          <NavLink to="/download" cn="mx-4 flex">
            <button className="text-white hover:text-[white] hover:scale-110 duration-500 font-bold">
              DOWNLOAD
            </button>
          </NavLink>
          <NavLink to="/contact" cn="mx-4 flex">
            <button className="text-white hover:text-[white] hover:scale-110 duration-500 font-bold">
              CONTACT
            </button>
          </NavLink>
          <a href="https://discord.gg/kP4ecaR4" target="_blank">
            <img
              className="mx-8 hover:scale-110 duration-500"
              height={35}
              width={30}
              src="discord-logo.png"
              alt="discord logo"
            />
          </a>

          <a href="https://github.com/SaptarshiSarkar12/Drifty" target="_blank">
            <img
              className="mx-8 hover:scale-110 duration-500"
              height={30}
              width={35}
              src="github-logo.png"
              alt="github logo"
            />
          </a>
        </div>
      </nav>
    </header>
  );
}
