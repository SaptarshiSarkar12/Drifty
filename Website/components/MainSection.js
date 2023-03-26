import Image from "next/image"
export default function MainSection() {
    return (
        <div className="flex flex-row-1 bg-top space-evenly lg:justify-center xs:justify-start">
            <div className="lg:flex lg:w-1/2">
                <div className=" lg:flex lg:flex-col  text-gray-800 py-20 px-6 md:text" >
                    <h1 className="text-8xl lg:ml-20 font-extrabold text-blue-700 mt-0 mb-6">Drifty</h1>
                    <p className="text-2xl lg:ml-20 font-sans mb-8 ">Drifty is an open-source interactive File Downloader system built
                        with Java. It takes the link to the file, the directory where it
                        needs to be saved and the filename of the downloaded file as input
                        and downloads it in the given directory with the given filename.</p>
                    <div className="flex space-x-2 justify-start lg:ml-20 lg:mt-3 lg:mb-16">
                        <a className="inline-block px-6 py-3.5 w-30 h-14 bg-blue-700 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:bg-yellow-400 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-100 ease-in-out" href="#!" role="button">More Info</a>
                        <a className="inline-block px-6 py-3.5 w-30 h-14 bg-blue-700 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:bg-yellow-400 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-100 ease-in-out" href="#!" role="button">Demo</a>
                    </div>
                </div>
            </div>
            <div className="hidden flex-col-1 lg:block text-center text-gray-800 py-20 px-6 img-layer w-1/2" id="ImgLayer">
                <Image width={500} height={0} className=" absolute  animate-img" src="/Resources/Layers/2.webp" alt="..." />
                <Image width={500} height={0} className=" absolute animate-img hover:bg-black" src="/Resources/Layers/3.webp" alt="..." />
                <Image width={500} height={0} className=" absolute  animate-img" src="/Resources/Layers/4.webp" alt="..." />
                <Image width={500} height={0} className=" absolute  animate-img" src="/Resources/Layers/5.webp" alt="..." />
                <Image width={500} height={0} className=" absolute  animate-img" src="/Resources/Layers/6.webp" alt="..." />
                <Image width={500} height={0} className=" absolute  animate-img" src="/Resources/Layers/7.webp" alt="..." />
            </div>
        </div>

    )
}