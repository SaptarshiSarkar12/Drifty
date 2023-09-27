import React from "react";
import Image from "next/image";
import "font-awesome/css/font-awesome.min.css";

function Features() {
  return (
    <div className="sectionWrapper py-6 px-12 background bg-gradient-to-r from-cyan-500 to-blue-500 text-white font-sans select-none">
      <h1 className="text-center mb-6 font-extrabold text-4xl pt-6">
        Features
      </h1>
      <div className="featuresContainer column-1 flex flex-wrap justify-center">
        <div className="feature m-2 p-4 rounded-md h-max flex flex-col justify-center shadow-lg bg-slate-100 text-black w-60">
          <i
            className="fa fa-unlock-alt self-center mb-5"
            aria-hidden="true"
            style={{ fontSize: "3rem" }}
          ></i>
          <h3 className="feature--title text-lg mb-1">
            It&apos;s Free and Open-Source
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty is both free and open-source, so, you can bring new features
            that you wish to have 🎉
          </p>
        </div>

        <div className="feature m-2 p-4 rounded-md h-max flex flex-col justify-center shadow-lg bg-slate-100	 text-black w-60">
          <i
            className="fa fa-download self-center mb-5"
            aria-hidden="true"
            style={{ fontSize: "3rem" }}
          ></i>

          <h3 className="feature--title text-lg  mb-1">
            Faster Downloading of files
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty supports parallel downloading using multiple threads
          </p>
        </div>
        <div className="feature m-2 p-4 rounded-md h-max flex flex-col justify-center shadow-lg bg-slate-100	 text-black w-60">
          <i
            className="fa fa-youtube-play self-center mb-5"
            aria-hidden="true"
            style={{ fontSize: "3rem" }}
          ></i>

          <h3 className="feature--title text-lg  mb-1">
            Support for downloading videos from YouTube, Instagram, etc.
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty allows downloading videos from popular platforms like YouTube
            and Instagram with ease.
          </p>
        </div>
        <div className="feature m-2 p-4 rounded-md h-max flex flex-col justify-center shadow-lg bg-slate-100	 text-black w-60">
          <i
            className="fa fa-desktop self-center mb-5"
            aria-hidden="true"
            style={{ fontSize: "3rem" }}
          ></i>
          <h3 className="feature--title text-lg mb-1">
            Available both in GUI and CLI mode
          </h3>
          <p className="feature--description font-normal leading-normal">
            Drifty has both CLI and GUI mode. So, it can be used anywhere as CLI
            or as GUI according to the requirements of the user.
          </p>
        </div>
      </div>
      <div className="text-2xl font-bold flex justify-center my-4">
        ....and many more!
      </div>
    </div>
  );
}

export default Features;
