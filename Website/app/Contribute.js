"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import Link from "next/link";

export default function Contribute({ props }) {
  const [values, setValues] = useState([]);
  useEffect(() => {
    fetch(
      "https://api.github.com/search/repositories?q=user:SaptarshiSarkar12+repo:Drifty+Drifty",
    )
      .then((res) => res.json())
      .then((data) => {
        setValues([data.items[0].stargazers_count, data.items[0].forks_count]);
      });
  }, []);
  let totalNoOfContributors = props.contrib.length;
  return (
    <div
      id="contribute"
      className="flex flex-col justify-center items-center gap-12 py-12 px-4  bg-var select-none text-white"
    >
      <div className="flex flex-col gap-4">
        <h2 className="text-3xl sm:text-4xl font-bold text-center">
          More Information About Drifty
        </h2>
        <p className="max-w-2xl text-xl lg:text-2xl text-center font-sans leading-normal">
          It is available both in CLI (Command Line Interface) and GUI
          (Graphical User Interface) mode for all the major platforms like
          Windows, Linux and MacOS. We believe in team work. Any contribution
          that brings value to the project is highly appreciated. You can look
          into the{" "}
          <Link
            className={
              "font-bold bg-gradient-to-r from-pink-500 to-yellow-400 text-transparent bg-clip-text"
            }
            target={"_blank"}
            href={"https://github.com/users/SaptarshiSarkar12/projects/3"}
          >
            roadmap
          </Link>{" "}
          to know about the issues to work on,
          <b
            className={
              "bg-gradient-to-r from-green-500 to-green-400 text-transparent bg-clip-text"
            }
          >
            {" "}
            in progress
          </b>{" "}
          and completed.
        </p>
      </div>
      <div className="flex flex-col gap-8">
        <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-center">
          Be a part of Drifty Family!
        </h2>
        <div className="flex items-center justify-center gap-[2em] flex-wrap rounded-full gap-y-4 justify-items-center xs:pb-5 pr-1">
          {props.contrib.map((item, index) => {
            if (index < 7 && item.type === "User") {
              // We are using 7 instead of 6 because we are not counting the dependabot
              return (
                <a href={item.html_url} key={index}>
                  <Image
                    className="rounded-full border-2 border-transparent hover:transition ease-in-out hover:border-[#ffffff70] delay-150 duration-300"
                    width={64}
                    height={64}
                    src={item.avatar_url + ".webp&s=77"}
                    alt={item.login}
                  ></Image>
                </a>
              );
            }
          })}
          <a
            href="https://github.com/SaptarshiSarkar12/Drifty/graphs/contributors"
            target="_blank"
          >
            <div className="py-2 px-3.5 gap-1 rounded-full h-16 w-16 text-xl font-semibold flex items-center justify-center text-center xs:transition ease-in-out delay-150 bg-blue-500 hover:bg-[#2e51ab] duration-300">
              {totalNoOfContributors - 6}+
            </div>
          </a>
        </div>
        <div className="flex items-center justify-center flex-wrap text-lg font-semibold gap-6">
          <a
            className="py-2 px-3.5 rounded-lg w-auto text-center xs:transition ease-in-out delay-100 bg-blue-500 hover:bg-[#2e51ab] duration-300"
            href="https://github.com/SaptarshiSarkar12/Drifty"
          >
            <i aria-hidden="true" className="fab fa-github p-2"></i>Contribute
          </a>
          <a
            className="py-2 px-3.5 rounded-lg w-auto text-center xs:transition ease-in-out delay-100 bg-blue-500 hover:bg-[#2e51ab] duration-300"
            href="https://github.com/SaptarshiSarkar12/Drifty/stargazers"
          >
            <i aria-hidden="true" className="fa fa-regular fa-star p-2"></i>
            Stars : {values[0]}
          </a>
          <a
            className="py-2 px-3.5 rounded-lg w-auto text-center xs:transition ease-in-out delay-100 bg-blue-500 hover:bg-[#2e51ab] duration-300"
            href="https://github.com/SaptarshiSarkar12/Drifty/network/members"
          >
            <i
              aria-hidden="true"
              className="fa fa-duotone fa-code-fork p-2"
            ></i>
            Forks : {values[1]}
          </a>
        </div>
      </div>
    </div>
  );
}
