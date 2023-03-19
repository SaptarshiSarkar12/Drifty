export default function MainSection() {
    return (
        <div className="flex flex-row-1 bg-top space-evenly justify-center">
            <div className="flex w-1/2">
                <div className=" flex flex-col  text-gray-800 py-20 px-6">
                    <h1 className="text-8xl md:ml-20 font-extrabold text-blue-700 mt-0 mb-6">Drifty</h1>
                    <p className="text-2xl md:ml-20 font-sans mb-8">Drifty is an open-source interactive File Downloader system built
                        with Java. It takes the link to the file, the directory where it
                        needs to be saved and the filename of the downloaded file as input
                        and downloads it in the given directory with the given filename.</p>
                    <div className="flex space-x-2 justify-start md:ml-20 md:mt-3 md:mb-16">
                        <a className="inline-block px-6 py-3.5 w-30 h-14 bg-blue-700 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:bg-yellow-400 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-100 ease-in-out" href="#!" role="button">More Info</a>
                        <a className="inline-block px-6 py-3.5 w-30 h-14 bg-blue-700 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:bg-yellow-400 hover:shadow-lg  focus:shadow-lg focus:outline-none focus:ring-0 active:bg-blue-400 active:shadow-lg transition duration-100 ease-in-out" href="#!" role="button">Demo</a>
                    </div>
                </div>
            </div>
            <div className="hidden flex-col-1 md:block text-center text-gray-800 py-20 px-6 img-layer w-1/2" id="ImgLayer">
                <img className=" -mt-28" src="Resources/Layers/2.webp" alt="..." />
                <img src="Resources/Layers/3.webp" alt="..." srcset="" />
                <img src="Resources/Layers/4.webp" alt="..." />
                <img src="Resources/Layers/5.webp" alt="..." />
                <img src="Resources/Layers/6.webp" alt="..." />
                <img src="Resources/Layers/7.webp" alt="..." />
            </div>
        </div>

    )
}