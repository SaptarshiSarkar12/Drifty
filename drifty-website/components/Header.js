import Image from "next/image";
import { useState } from "react"

function NavLink({ to, children, cn }) {
    return <a href={to} className={`text-gray-900 hover:text-black ${cn}`}>
        {children}
    </a>
}

function MobileNav({ open, setOpen }) {
    return (
        <div className={`absolute top-0 left-0 h-screen w-screen bg-white transform ${open ? "-translate-x-0" : "-translate-x-full"} transition-transform duration-300 ease-in-out filter drop-shadow-md `}>
            <div className="flex items-center justify-center filter drop-shadow-md bg-blue-100 h-20"> {/*logo container*/}
                <a className="text-xl font-semibold" href="/"> <Image src="/favicons/favicon-32x32.png" width={32} height={32} alt="DriftyLogo"/></a>
            </div>
            <div className="flex flex-col ml-4">
                <NavLink to="/contact" cn="text-xl font-normal my-4">About</NavLink>
                <NavLink to="/contact" cn="text-xl font-normal my-4">CONTRIBUTE</NavLink>
                <NavLink to="/about" cn="text-xl font-normal my-4">DOWNLOAD</NavLink>
                <NavLink to="/contact" cn="text-xl font-normal my-4">Contact</NavLink>
            </div>
        </div>
    )
}

export default function Header() {
    const [open, setOpen] = useState(false);
    return (
        <header>
            <nav className="flex filter drop-shadow-md bg-white-50 px-4 py-4 h-20 items-center">
                <MobileNav open={open} setOpen={setOpen} />
                <div className="w-3/12 flex items-center">
                    <a className="text-2xl font-semibold" href="/"><img src="favicons/favicon-32x32.png" /></a>
                </div>
                <div className="w-9/12 flex justify-end items-center">
                    <div className="z-50 flex relative w-8 h-8 flex-col justify-between items-center md:hidden" onClick={() => { setOpen(!open) }}>
                        {/* hamburger button */}
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "rotate-45 translate-y-3.5" : ""}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transition-all duration-300 ease-in-out ${open ? "w-0" : "w-full"}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "-rotate-45 -translate-y-3.5" : ""}`} />
                    </div>
                    <div className="hidden md:flex">
                        <NavLink to="/contact" cn="mx-4">HOME</NavLink>
                        <NavLink to="/about" cn="mx-4">ABOUT</NavLink>
                        <NavLink to="/contact" cn="mx-4">CONTRIBUTE</NavLink>
                        <NavLink to="/about" cn="mx-4">DOWNLOAD</NavLink>
                        <NavLink to="/contact" cn="mx-4">CONTACT</NavLink>
                    </div>
                </div>
            </nav>
            <div className="text-center bg-white-50 text-gray-800 py-20 px-6">
                <h1 className="text-5xl font-bold text-blue-700 mt-0 mb-6">Drifty</h1>
                <p className="text-3xl font-sans mb-8">Drifty is an open-source interactive File Downloader system built
        with Java. It takes the link to the file, the directory where it
        needs to be saved and the filename of the downloaded file as input
        and downloads it in the given directory with the given filename.</p>
               <div className="flex space-x-2 justify-center">
               <a className="inline-block px-6 py-2.5 bg-blue-600 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out" data-mdb-ripple="true" data-mdb-ripple-color="light" href="#!" role="button">More Info</a>
                <a className="inline-block px-6 py-2.5 bg-blue-600 text-white font-medium text-xs leading-tight uppercase rounded shadow-md hover:bg-blue-700 hover:shadow-lg focus:bg-blue-700 focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-800 active:shadow-lg transition duration-150 ease-in-out" data-mdb-ripple="true" data-mdb-ripple-color="light" href="#!" role="button">Demo</a>
               </div> 
            </div>
        </header>
    )
}