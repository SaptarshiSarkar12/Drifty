import React from "react";

function Features() {
  return (
    <div className="sectionWrapper py-6 px-12 background bg-top text-white font-sans">
      <h1 className="text-center mb-6 font-extrabold text-4xl pt-6 select-none">
        What we are offering:
      </h1>
      <div className="featuresContainer column-1 flex flex-wrap justify-center">
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg">
          <img
            src="./features-images/open-source.png"
            alt="Open Source Icon"
            className="w-1/4 self-center mb-5"
          />
          <h3 className="feature--title text-lg mb-1">
            It's Free and Open-Source
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty is both free and open-source, giving you the liberty to use,
            modify, and share it with the world.
          </p>
        </div>

        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg">
          <img
            src="./features-images/download.png"
            alt="Faster Downloading Icon"
            className="w-1/4 self-center mb-5"
          />
          <h3 className="feature--title text-lg  mb-1">
            Faster Downloading of files
          </h3>
          <p className="feature--description font-normal leading-normal">
            Parallel downloading of files' part makes the download faster
          </p>
        </div>
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg">
          <img
            src="./features-images/youtube.png"
            alt="Support for Youtube, Instagram Icon"
            className="w-1/4 self-center mb-5"
          />
          <h3 className="feature--title text-lg  mb-1">
            Support for downloading videos from YouTube, Instagram, etc.
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty allows downloading videos from popular platforms like YouTube
            and Instagram with ease.
          </p>
        </div>
        <div className="feature w-1/4 m-2 p-4 rounded-md h-auto flex flex-col justify-center shadow-lg">
          <img
            src="./features-images/user-interface.png"
            alt="Gui and Cli Icon"
            className="w-1/4 self-center mb-5"
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
    </div>
  );
}

export default Features;
