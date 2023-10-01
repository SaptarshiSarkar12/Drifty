import Image from "next/image";
import Link from "next/link";

export default function MainSection() {
  return (
    <div className="flex flex-row-1 bg-top space-evenly lg:justify-start xs:justify-start  select-none">
      <div className="lg:flex lg:w-1/2">
        <div className=" lg:flex lg:flex-col text-white py-20 px-6 md:text">
          <div className="flex lg:ml-20 border-b-4 pb-3 gap-5">
            <img
              className="shadow-sm"
              width={110}
              height={105}
              src="Drifty1024.png"
              alt="Drifty Logo"
            />
            <h1 className="text-8xl font-bold">Drifty</h1>
          </div>
          <p className="text-2xl lg:ml-20 font-sans my-8">
            Drifty is an Open-Source interactive File Downloader system built
            with Java. It takes the link of the file to be downloaded and
            downloads it in the appropriate folder with the appropriate filename
            retrieved from the link.
            <br />
            <br />
            It can download files from platforms including YouTube, Instagram,
            and many more.
          </p>

          <div className="flex space-x-3 justify-start lg:ml-20 lg:mt-3 lg:mb-16">
            <Link
              className="border-2 px-5 py-3.5 text-white rounded hover:border-[#191B33] hover:bg-[#191B33] hover:scale-105 duration-500"
              href="/download"
              role="button"
            >
              Download
            </Link>
            <Link
              className="border-2 px-5 py-3.5 text-white rounded hover:border-[#191B33] hover:bg-[#191B33] hover:scale-105 duration-500"
              href="/#demo"
              role="button"
            >
              View Demo
            </Link>
          </div>
        </div>
      </div>
      <div
        className="xs:hidden mx-auto flex-col-1 lg:block text-center text-gray-800 py-20 px-6 img-layer w-1/2"
        id="ImgLayer"
      >
        <Image
          width={500}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/2.webp"
          alt="..."
        />
        <Image
          width={500}
          height={0}
          className="absolute animate-img hover:bg-black"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/3.webp"
          alt="..."
        />
        <Image
          width={500}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/4.webp"
          alt="..."
        />
        <Image
          width={500}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/5.webp"
          alt="..."
        />
        <Image
          width={500}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/6.webp"
          alt="..."
        />
        <Image
          width={500}
          height={0}
          className="absolute animate-img"
          src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Layers/7.webp"
          alt="..."
        />
      </div>
    </div>
  );
}
