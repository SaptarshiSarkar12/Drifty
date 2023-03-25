import Image from "next/image";
import { useState } from "react"
import Link from "next/link";
import { useCallback,useEffect } from "react";
function NavLink({ to, children, cn }) {
    return <Link scroll={false} href={to} className={`text-gray-900 hover:text-black ${cn}`}>
        {children}
    </Link>
}

function MobileNav({ open, setOpen }) {
    return (
        <div className={`absolute top-0 left-0 h-screen w-screen bg-top transform ${!open && "-translate-x-full"} transition-transform duration-300 ease-in-out filter drop-shadow-md`}>
            <div className="flex items-start justify-center filter bg-top h-20"> {/*logo container*/}
                <Link className="text-xl font-semibold" href="/"> <Image src="/favicons/favicon-32x32.png" width={32} height={32} alt="DriftyLogo"/></Link>
            </div>
            <div className="grid grid-rows-4 justify-items-center bg-top">
                <NavLink to="/about" cn="text-xl font-normal my-4"><button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:bg-blue-700  hover:shadow-lg">About</button></NavLink>
                <NavLink to="/contributions" cn="text-xl font-normal my-4"><button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:bg-blue-700  hover:shadow-lg">Contribute</button></NavLink>
                <NavLink to="/download" cn="text-xl font-normal my-4"><button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:bg-blue-700  hover:shadow-lg">Download</button></NavLink>
                <NavLink to="/contact" cn="text-xl font-normal my-4"><button className="p-2 w-28 h-11 rounded-full  bg-btn-color hover:bg-blue-700  hover:shadow-lg">Contact</button></NavLink>
            </div>
        </div>
    )
}

export default function Header({props}) {
    const [open, setOpen] = useState(false);
    const[hcolor,setHcolor]=useState(props+" pt-7")
    const onScroll = useCallback(event => {
        const { pageYOffset, scrollY } = window;
        // console.log(props);
        if(scrollY<10) setHcolor(props+" pt-7");
        else setHcolor("bg-var shadow-lg pt-4");
    },[]);
  
    useEffect(() => {
      //add eventlistener to window
      window.addEventListener("scroll", onScroll, { passive: true });
      // remove event on unmount to prevent Linkmemory leak with the cleanup
      return () => {
         window.removeEventListener("scroll", onScroll, { passive: true });
      }
    });
    return (
        <header className={`md:sticky top-0 ${hcolor} z-40` }>
            <nav className="flex filter drop-shadow-md  px-4 py-4 h-20 items-center">
                <MobileNav open={open} setOpen={setOpen} />
                <div className="w-4/12 flex items-center lg:pl-20">
                    <Link className="font-semibold" href="/"><Image className=" w-12" src="/favicons/favicon-32x32.png" alt="Logo" width={32} height={32}/></Link>
                </div>
                <div className="w-9/12 flex justify-end items-center">
                    <div className="z-40 flex relative w-8 h-8 flex-col justify-between items-center md:hidden" onClick={() => { setOpen(!open) }}>
                        {/* hamburger button */}
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "rotate-45 translate-y-3.5" : ""}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transition-all duration-300 ease-in-out ${open ? "hidden" : "w-full"}`} />
                        <span className={`h-1 w-full bg-black rounded-lg transform transition duration-300 ease-in-out ${open ? "-rotate-45 -translate-y-3.5" : ""}`} />
                    </div>
                    <div className="hidden md:flex mr-20">
                    <NavLink to="/" cn="mx-4"><button className="p-2 rounded-full shadow-lg bg-btn-color hover:bg-blue-700">HOME</button></NavLink>
                        <NavLink to="/about" cn="mx-4"><button className="p-2 rounded-full shadow-lg bg-btn-color hover:bg-blue-700">ABOUT</button></NavLink>
                        
                        <NavLink to="/contributions" cn="mx-4"><button className="p-2 rounded-full shadow-lg bg-btn-color hover:bg-blue-700">CONTRIBUTE</button></NavLink>
                        <NavLink to="/download" cn="mx-4"><button className="p-2 rounded-full shadow-lg bg-btn-color hover:bg-blue-700">DOWNLOAD</button></NavLink>
                        <NavLink to="/contact" cn="mx-4"><button className="p-2 rounded-full shadow-lg bg-btn-color hover:bg-blue-700">CONTACT</button></NavLink>
                    </div>
                </div>
            </nav>
            
        </header>
    )
}