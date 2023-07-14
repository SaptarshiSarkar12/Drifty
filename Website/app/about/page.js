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
                        <Image width={360} height={25} src="https://github-readme-stats.vercel.app/api?username=saptarshisarkar12&show_icons=true&theme=tokyonight&show=reviews" alt=""/>
                        <Image width={360} height={25} src="https://github-readme-activity-graph.vercel.app/graph?username=SaptarshiSarkar12&bg_color=0f2d3d&color=1cadfb&line=1cadfb&point=1cadfb&area=true&hide_border=true" alt="GitHub Contribution graph of Saptarshi Sarkar" />
                        <Image width={360} height={25} src="https://github-readme-streak-stats.herokuapp.com/?user=saptarshisarkar12&theme=tokyonight" alt="" />                        
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    )
}