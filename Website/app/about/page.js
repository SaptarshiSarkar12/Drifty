import Footer from "../Footer"
import Header from "../Header"
import Image from "next/image"


export const metadata = {
    title: "About",
    description: 'About Saptarshi Sarkar, the developer of Drifty'
}

export default function about(){
    return(
        <div className={"select-none"}>
            <Header props={"bg-top"}/>
            <div className="bg-gradient-to-b from-top from-18% via-about via-12% to-bottom to-12%">
                <h1 className="text-center font-bold text-5xl pt-8 sm:pl-8">About</h1>
                <div className="grid justify-center sm:pl-8 pt-10">
                    <Image width="0" height="0" style={{ width: '100%', height: 'auto' }} className="rounded-full" src="https://avatars.githubusercontent.com/u/105960032?v=4.webp&s=200" alt="GitHub Profile Picture of Saptarshi Sarkar" />
                </div>
                <div className="grid justify-center xs:pl-3 sm:pl-16">
                    <a className="place-self-center md:pl-24 sm:place-self-auto xs:place-self-center pt-4 font-bold" href="https://bio.link/saptarshi">Saptarshi Sarkar</a>
                    <p className="pt-3 sm:mr-7 text-xl font-bold bg-gradient-to-r from-pink-500 to-orange-400 text-transparent bg-clip-text">Open Source Software Developer</p>
                </div>
                <div className="bg-var mx-auto mt-4 h-fit w-4/5 rounded-lg">
                    <p className="p-6 text-xl text-white text-center">I am a passionate Software Developer and an open-source enthusiast building this interactive file downloading system called <a className="bg-gradient-to-r from-orange-500 to-yellow-400 text-transparent bg-clip-text" href="https://saptarshisarkar12.github.io/Drifty/"><b>Drifty</b></a> using Java. I am open for collaboration on open-source projects. I love contributing to open-source projects and enjoy maintaining an open-source project.</p>
                </div>
                <div className="grid justify-center mt-5 pb-5">
                    <a className="bg-white rounded-lg p-2 border-2 border-black hover:transition ease-in-out duration-300 delay-100 hover:-translate-y-1 hover:scale-110 hover:text-white hover:bg-black" href="https://twitter.com/SSarkar2007"><button><i className="fab fa-x-twitter" aria-hidden="true"></i> Let&apos;s Connect</button></a>
                </div>
                <div className="mt-5 mx-5 border-t-2 border-black">
                    <h1 className="text-center font-bold text-4xl py-5">My Stats</h1>
                    <div className="grid w-auto h-auto lg:grid-cols-3  lg:-space-x-25 justify-items-center items-center gap-2 py-5 pb-20 md:grid-cols-1">
                        <Image width="0" height="0" style={{ width: '100%', height: 'auto' }} src="https://camo.githubusercontent.com/bfb749217193aa06c1ba75f130ad01c6d67a0c596c604b1bea8dd9509cabf097/68747470733a2f2f6769746875622d726561646d652d73746174732e76657263656c2e6170702f6170693f757365726e616d653d7361707461727368697361726b617231322673686f775f69636f6e733d74727565267468656d653d746f6b796f6e696768742673686f773d72657669657773" alt=""/>
                        <Image width="0" height="0" style={{ width: '100%', height: 'auto' }} src="https://camo.githubusercontent.com/a1774c77529d275b27f41ee35b03f0e99ae219328594fa8b9c54efa78851de96/68747470733a2f2f6769746875622d726561646d652d61637469766974792d67726170682e76657263656c2e6170702f67726170683f757365726e616d653d5361707461727368695361726b617231322662675f636f6c6f723d30663264336426636f6c6f723d316361646662266c696e653d31636164666226706f696e743d31636164666226617265613d7472756526686964655f626f726465723d74727565" alt="GitHub Contribution graph of Saptarshi Sarkar" />
                        <Image width="0" height="0" style={{ width: '100%', height: 'auto' }} src="https://camo.githubusercontent.com/1825f31f8281e10d9490f2df42bfd4d3ba9bb6923bbf11bee69e38c4b0e14bfb/68747470733a2f2f6769746875622d726561646d652d73747265616b2d73746174732e6865726f6b756170702e636f6d2f3f757365723d7361707461727368697361726b61723132267468656d653d746f6b796f6e69676874" alt="" />
                   </div>
                </div>
            </div>
            <Footer />
        </div>
    )
}
