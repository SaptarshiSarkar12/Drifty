"use client";
import Image from "next/image";
import { useState } from "react"
import Link from "next/link";
import { useCallback,useEffect } from "react";
function NavLink({ to, children, cn }) {
    return <Link scroll={false} href={to} className={`text-gray-900 hover:text-black ${cn}`}>
        {children}
    </Link>
}

function MobileNav({ open}) {
    return (
        <div className={`absolute z-10 top-0 -mt-7 pt-2 left-0 h-screen w-screen bg-top transform ${!open && "-translate-x-full"} transition-transform duration-300 ease-in-out filter drop-shadow-md`}>
            <div className="flex items-start justify-center filter bg-top h-30"> {/*logo container*/}
                <Link className="text-xl font-semibold" href="/"><Image src="Drifty1024.png" width={80} height={80} alt="Logo of Drifty"/></Link>
            </div>
            <div className="grid grid-rows-3 justify-items-center bg-top">
                <NavLink to="/about" cn="text-xl font-normal my-4">
                	<button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300  hover:shadow-lg">About</button>
        	</NavLink>
                <NavLink to="/download" cn="text-xl font-normal my-4">
                	<button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300  hover:shadow-lg">Download</button>
        	</NavLink>
                <NavLink to="/contact" cn="text-xl font-normal my-4">
                	<button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300  hover:shadow-lg">Contact</button>
        	</NavLink>
            </div>
        </div>
    )
}

export default function Header({props}) {
    const [open, setOpen] = useState(false);
    const [hcolor,setHcolor] = useState(props+" pt-7")
    const onScroll = useCallback(() => {
        const { scrollY} = window;
        if(scrollY === 0) setHcolor(props+" pt-7");
        else setHcolor("bg-var shadow-lg pt-4");
    },[props]);
  
    useEffect(() => {
      //add event listener to window
      window.addEventListener("scroll", onScroll, { passive: true });
      // remove event on unmounting to prevent Linkmemory leak with the cleanup
    });
    return (
        <header className={`md:sticky top-0 ${hcolor} z-40 select-none` }>
            <nav className="flex filter drop-shadow-md  px-4 py-4 h-20 items-center">
                <MobileNav open={open} setOpen={setOpen} />
                <div className="w-4/12 flex items-center lg:pl-20">
                    <Link className="font-semibold" href="/"><Image className=" w-16" src="Drifty1024.png" alt="Logo Of Drifty" width={300} height={300}/></Link>
                </div>
                <div className="w-9/12 flex justify-end items-center">
                    <div className="z-40 flex relative w-8 h-8 flex-col justify-between items-center md:hidden" onClick={() => { setOpen(!open) }} role={"button"}>
                        {/* hamburger button */}
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "rotate-45 translate-y-3.5" : ""}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transition-all duration-300 ease-in-out ${open ? "hidden" : "w-full"}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "-rotate-45 -translate-y-3.5" : ""}`} />
                    </div>
                    <div className="hidden md:flex mr-20">
                    	<NavLink to="/#" cn="mx-4">
                    		<button className="p-2 rounded-full shadow-lg bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300">HOME</button>
            		    </NavLink>
                    	<NavLink to="/about" cn="mx-4">
                    		<button className="p-2 rounded-full shadow-lg bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300">ABOUT</button>
                    	</NavLink>
                    	<NavLink to="/download" cn="mx-4">
                    		<button className="p-2 rounded-full shadow-lg bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300">DOWNLOAD</button>
                    	</NavLink>
                    	<NavLink to="/contact" cn="mx-4">
                    		<button className="p-2 rounded-full shadow-lg bg-btn-color hover:-translate-y-1 hover:scale-110 hover:bg-indigo-500 duration-300">CONTACT</button>
                    	</NavLink>
                    </div>
                </div>
            </nav>            
        </header>
    )
}
