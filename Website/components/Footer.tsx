"use client";
import Link from "next/link";
import { FaDiscord, FaGithub } from "react-icons/fa";
import DarkModeToggle from "./DarkModeToggle";

export default function Footer() {
  return (
    <footer className="dark:bg-(--dark-accent) bg-(--light-accent) py-4 mt-8 border-t border-opacity-50 shadow-md">
      <div className="container mx-auto flex flex-col md:flex-row justify-between items-center px-8">
        <div className="flex-1 text-center md:text-left">
          <p className="text-sm font-medium tracking-wide">
            © {new Date().getFullYear()} Drifty. All rights reserved.
          </p>
        </div>

        <ul className="flex-auto flex flex-wrap justify-center md:justify-center space-x-4 text-sm font-medium">
          <li>
            <DarkModeToggle />
          </li>
          <li>
            <Link
              href="/privacy-policy"
              className="hover:underline transition-all transform hover:scale-105"
            >
              Privacy Policy
            </Link>
          </li>
          <li>
            <Link
              href="/terms"
              className="hover:underline transition-all transform hover:scale-105"
            >
              Terms of Service
            </Link>
          </li>
          <li>
            <Link
              href="/contact"
              className="hover:underline transition-all transform hover:scale-105"
            >
              Contact
            </Link>
          </li>
          <li>
            <Link
              href="/disclaimer"
              className="hover:underline transition-all transform hover:scale-105"
            >
              Disclaimer
            </Link>
          </li>
        </ul>

        <div className="flex-1 flex justify-center md:justify-end space-x-6">
          <a
            href="https://discord.gg/DeT4jXPfkG"
            className="hover:opacity-75 transition-all transform hover:scale-110"
            aria-label="Discord"
            target="_blank"
          >
            <FaDiscord size={26} />
          </a>
          <a
            href="https://github.com/SaptarshiSarkar12/Drifty"
            className="hover:opacity-75 transition-all transform hover:scale-110"
            aria-label="GitHub"
            target="_blank"
          >
            <FaGithub size={26} />
          </a>
        </div>
      </div>
    </footer>
  );
}
