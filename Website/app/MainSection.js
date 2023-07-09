import Image from "next/image"
import Link from "next/link"

export default function MainSection() {
    return (
        <div className="flex flex-row-1 bg-top space-evenly lg:justify-start xs:justify-start">
            <div className="lg:flex lg:w-1/2">
                <div className=" lg:flex lg:flex-col  text-white py-20 px-6 md:text" >
                    <h1 className="text-8xl lg:ml-20 font-extrabold text-white  mt-0 mb-6">Drifty</h1>
                    <p className="text-2xl  lg:ml-20 font-sans mb-8 ">Drifty is an open-source interactive File Downloader system built
                        with Java. It takes the link to the file to be downloaded, the directory where it
                        needs to be saved and the filename of the downloaded file as input
                        and downloads it in the given directory with the given filename.</p>
                    <div className="flex space-x-3 justify-start lg:ml-20 lg:mt-3 lg:mb-16">
                        <Link className="inline-block px-6 py-3.5 w-30 h-14 bg-gradient-to-r from-blue-800 via-blue-600 to-blue-800 text-white font-medium text-lg leading-tight rounded-lg shadow-md  hover:from-pink-500 hover:to-yellow-500 hover:transition ease-in-out duration-300 delay-100 hover:-translate-y-1 hover:scale-110 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition ease-in-out" href="/#contrib" role="button">More Info</Link>
                        <Link className="inline-block px-6 py-3.5 w-30 h-14 bg-gradient-to-r from-blue-800 via-blue-600 to-blue-800 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:from-pink-500 hover:to-yellow-500 hover:transition ease-in-out duration-300 delay-100 hover:-translate-y-1 hover:scale-110 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition ease-in-out" href="/#demo" role="button">Demo</Link>
                    </div>
                </div>
            </div>
            <div className="xs:hidden flex-col-1 lg:block text-center text-gray-800 py-20 px-6 img-layer w-1/2" id="ImgLayer">
                <Image width={500} height={0} className="absolute animate-img" src="/Layers/2.webp" alt="..." />
                <Image width={500} height={0} className="absolute animate-img hover:bg-black" src="/Layers/3.webp" alt="..." />
                <Image width={500} height={0} className="absolute animate-img" src="/Layers/4.webp" alt="..." />
                <Image width={500} height={0} className="absolute animate-img" src="/Layers/5.webp" alt="..." />
                <Image width={500} height={0} className="absolute animate-img" src="/Layers/6.webp" alt="..." />
                <Image width={500} height={0} className="absolute animate-img" src="/Layers/7.webp" alt="..." />
            </div>
        </div>

    )
}