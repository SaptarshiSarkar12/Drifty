import React from "react";
import Image from "next/image";

function Features() {
  return (
    <div className="sectionWrapper py-6 px-12 background bg-var text-white font-sans">
      <h1 className="text-center mb-6 font-extrabold text-4xl pt-6 select-none">
        What we are offering:
      </h1>
      <div className="featuresContainer column-1 flex flex-wrap justify-center">
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg bg-slate-100	 text-black">
          <Image
            src="./features-images/open-source.png"
            width={100}
            height={100}
            alt="Open Source Icon"
            className="self-center mb-5"
          />
          <h3 className="feature--title text-lg mb-1">
            It&apos;s Free and Open-Source
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty is both free and open-source, giving you the liberty to use,
            modify, and share it with the world.
          </p>
        </div>

        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg bg-slate-100	 text-black">
          <Image
            src="./features-images/download.png"
            width={100}
            height={100}
            alt="Faster Downloading Icon"
            className="self-center mb-5"
          />
          <h3 className="feature--title text-lg  mb-1">
            Faster Downloading of files
          </h3>
          <p className="feature--description font-normal leading-normal">
            Parallel downloading of files&apos; part makes the download faster
          </p>
        </div>
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg bg-slate-100	 text-black">
          <Image
            src="./features-images/youtube.png"
            width={100}
            height={100}
            alt="Support for Youtube, Instagram Icon"
            className="self-center mb-5"
          />
          <h3 className="feature--title text-lg  mb-1">
            Support for downloading videos from YouTube, Instagram, etc.
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty allows downloading videos from popular platforms like YouTube
            and Instagram with ease.
          </p>
        </div>
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg bg-slate-100	 text-black">
          <Image
            src="./features-images/user-interface.png"
            width={100}
            height={100}
            alt="Gui and Cli Icon"
            className="self-center mb-5"
          />
          <h3 className="feature--title text-lg mb-1">
            Available both in GUI and CLI mod
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty has both CLI and GUI mode. So, it can be used anywhere as CLI
            or as GUI according to the requirements of the user.
          </p>
        </div>
      </div>
      <div class="text-2xl font-bold flex justify-center my-4">
        ....and many more!
      </div>
    </div>
  );
}

export default Features;
