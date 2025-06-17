"use client";

import { useTheme } from "next-themes";
import Image, { ImageProps } from "next/image";
import { useEffect, useState } from "react";

interface ThemeImageProps extends Omit<ImageProps, "src"> {
  lightSrc: string;
  darkSrc: string;
}

export default function ThemeImage({
  lightSrc,
  darkSrc,
  alt,
  ...props
}: ThemeImageProps) {
  const { theme, systemTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) return null;

  const resolvedTheme = theme === "system" ? systemTheme : theme;
  const src = resolvedTheme === "dark" ? darkSrc : lightSrc;

  return <Image src={src} alt={alt} {...props} />;
}
