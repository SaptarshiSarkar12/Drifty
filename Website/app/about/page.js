import Footer from "../Footer"
import Header from "../Header"
import Image from "next/image"

export const metadata = {
    title: "About",
    description: 'About Saptarshi Sarkar, the developer of Drifty',
    themeColor: [      
      { media: '(prefers-color-scheme: dark)', color: 'Medium Blue' },
    ],
    viewport: {
      width: 'device-width',
      initialScale: 1,
    },
}

export default function about(){
    return(
        <div>
            <Header props={"bg-top"}/>
            <div className="bg-gradient-to-b from-top from-18% via-about via-12% to-bottom to-12%">
                <h1 className="text-center font-bold text-5xl pt-8 sm:pl-8">About</h1>
                <div className="grid justify-center sm:pl-8 pt-10">
                    <Image width={200} height={80} className="rounded-full" src="https://avatars.githubusercontent.com/u/105960032?v=4.webp&s=200" alt="GitHub Profile Picture of Saptarshi Sarkar" />
                </div>
                <div className="grid justify-center xs:pl-3 sm:pl-16">
                    <a className="sm:pl-20 sm:place-self-auto xs:place-self-center sm: pt-4 font-bold" href="https://bio.link/saptarshi">Saptarshi Sarkar</a>
                    <p className="pt-3 sm:mr-7 text-orange-500 text-lg font-bold">Open Source Software Developer</p>
                </div>
                <div className="bg-var mx-auto mt-4 h-fit w-4/5 rounded-lg">
                    <p className="p-6 text-xl text-white text-center">I am a passionate Software Developer and an open-source enthusiast building this file downloading system called <a className="text-orange-400" href="https://saptarshisarkar12.github.io/Drifty/"><b>Drifty</b></a> using Java. I am open for collaboration on open-source projects. I love contributing to open-source projects and enjoy maintaining an open-source project.</p>
                </div>
                <div className="grid justify-center mt-5 pb-5">
                    <a className="bg-white rounded-lg p-2 border-2 border-black hover:transition ease-in-out duration-300 delay-100 hover:-translate-y-1 hover:scale-110 hover:text-white hover:bg-twitter-color" href="https://twitter.com/SSarkar2007"><button><i className="fab fa-twitter" aria-hidden="true"></i> Let&apos;s Connect</button></a>
                </div>
                <div className="mt-5 mx-5 border-t-2 border-black">
                    <h1 className="text-center font-bold text-3xl py-5">My Stats</h1>
                    <div className="grid grid-cols-3  lg:-space-x-25 justify-items-center py-5 pb-20">
                        <Image width={360} height={25} src="https://camo.githubusercontent.com/b85c78154ddce8eeafc3c3cfbc099af01cf7fb6b84c8bf1bc7e79c29d5323d40/68747470733a2f2f6769746875622d726561646d652d73746174732e76657263656c2e6170702f6170693f757365726e616d653d7361707461727368697361726b617231322673686f775f69636f6e733d74727565267468656d653d746f6b796f6e696768742673686f773d72657669657773" alt=""/>
                        <Image width={360} height={25} src="https://camo.githubusercontent.com/d9e7d29cb70a8d3044f58042b4595764d4f4d00fb0a386700bd9d71c14baa5c9/68747470733a2f2f6769746875622d726561646d652d61637469766974792d67726170682e76657263656c2e6170702f67726170683f757365726e616d653d5361707461727368695361726b617231322662675f636f6c6f723d30663264336426636f6c6f723d316361646662266c696e653d31636164666226706f696e743d31636164666226617265613d7472756526686964655f626f726465723d74727565" alt="GitHub Contribution graph of Saptarshi Sarkar" />
                        <Image width={360} height={25} src="https://camo.githubusercontent.com/d3db268ce53079d1d54d77e08e530839458b6d7600ee25c899f9037f617add1a/68747470733a2f2f6769746875622d726561646d652d73747265616b2d73746174732e6865726f6b756170702e636f6d2f3f757365723d7361707461727368697361726b61723132267468656d653d746f6b796f6e69676874" alt="" />                        
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    )
}
