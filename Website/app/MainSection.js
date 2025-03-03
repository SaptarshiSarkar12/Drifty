import Image from "next/image";
import Link from "next/link";

export default function MainSection() {
  return (
    <div className="flex flex-col bg-top space-y-6 lg:flex-row lg:space-y-0 lg:space-evenly py-[2rem] lg:justify-start xs:justify-start select-none">
      <div className="lg:w-1/2">
        <div className="lg:flex lg:flex-col text-white px-4 py-4 sm:pl-[5rem] md:text">
          <div className="flex lg:ml-4 lg:items-center border-b-4 pb-3 gap-2 ">
            <h1 className="text-6xl lg:text-7xl font-bold">Drifty</h1>
          </div>
          <p className="text-lg lg:text-2xl lg:ml-4 md:pr-20 font-sans my-4 xs:pb-2">
            Drifty is an Open-Source interactive File Downloader system built
            with Java. It takes the link of the file to be downloaded and
            downloads it in the appropriate folder with the appropriate filename
            retrieved from the link. It can download files from platforms
            including YouTube, Instagram, and many more.
          </p>

          <div className="flex gap-4 justify-start lg:ml-4 lg:mt-2 lg:mb-8">
            <Link
              className="px-3 py-2 text-sm md:text-base xl:text-xl bg-[#035792] hover:border-[#191B33] hover:bg-[#191B33] duration-500 rounded-lg"
              href={"/download"}
              role="button"
            >
              Download
            </Link>
            <Link
              className="px-3 py-2 text-sm md:text-base xl:text-xl bg-[#1d356333] hover:border-[#191B33] hover:bg-[#004f6a7d] duration-500 rounded-lg"
              href={"/#demo"}
              role="button"
            >
              View Demo
            </Link>
          </div>
        </div>
      </div>
      <div className="xs:hidden mx-auto lg:block text-center text-gray-800 py-4 px-2 img-layer w-1/2">
        <Image
          width={450}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/2.webp"
          alt="..."
        />
        <Image
          width={450}
          height={0}
          className="absolute animate-img hover:bg-black"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/3.webp"
          alt="..."
        />
        <Image
          width={450}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/4.webp"
          alt="..."
        />
        <Image
          width={450}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/5.webp"
          alt="..."
        />
        <Image
          width={450}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/6.webp"
          alt="..."
        />
        <Image
          width={450}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/7.webp"
          alt="..."
        />
      </div>
    </div>
  );
}