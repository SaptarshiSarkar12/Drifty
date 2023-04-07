import Footer from "/components/Footer"
import Header from "/components/Header"
import Image from "next/image"
export default function about(){
    return(
        <div>
             <Header props={"bg-about"}/>
             <div className="bg-about">
                <h1 className="text-center font-bold text-5xl pt-8 sm:pl-8">About</h1>
                <div className="grid justify-center sm:pl-8 pt-10">
                <Image width={200} height={80} className="rounded-full" src="https://avatars.githubusercontent.com/u/105960032?v=4" alt="avatar of Saptarshi Sarkar" />
                </div>
                <div className="grid justify-center xs:pl-3 sm:pl-16">
                <a className="sm:pl-20 sm:place-self-auto xs:place-self-center sm: pt-4 font-bold" href="https://bio.link/saptarshi">Saptarshi Sarkar</a>
                <p className="pt-3 sm:mr-7 text-orange-500 text-lg font-bold">Open Source Software Developer</p>
                </div>
                <div className="bg-var mx-auto mt-4 h-fit w-4/5 rounded-lg">
                <p className="p-6 text-xl text-white text-center">I am a passionate Software Developer and an open-source enthusiast building this file downloading system called <a className="text-orange-400" href="https://saptarshisarkar12.github.io/Drifty/"><b>Drifty</b></a> using Java. I am open for collaboration on open-source projects. I love contributing to open-source projects and enjoy maintaining an open-source project.</p>
                </div>
                <div className="grid justify-center mt-5 pb-5">
            <a className="bg-white rounded-lg p-2 border-2 border-black hover:bg-yellow-400" href="https://twitter.com/SSarkar2007"><button><i className="fab fa-twitter" aria-hidden="true"></i> Let&apos;s Connect</button></a>
        </div>
        <div className="mt-5 mx-5 border-t-2 border-black">
        <h1 className="text-center font-bold text-3xl py-5">My Stats</h1>
        <div className="grid grid-cols-2  lg:-space-x-44 justify-items-center py-5 pb-20">
        <Image width={360} height={25} className="lg:ml-20"  src="/Resources/github-stats-1.svg" alt=""/>
        <Image width={360} height={25} src="https://camo.githubusercontent.com/d3db268ce53079d1d54d77e08e530839458b6d7600ee25c899f9037f617add1a/68747470733a2f2f6769746875622d726561646d652d73747265616b2d73746174732e6865726f6b756170702e636f6d2f3f757365723d7361707461727368697361726b61723132267468656d653d746f6b796f6e69676874" alt=""></Image>
        </div>
        </div>
             </div>
             <Footer />
        </div>
    )
}