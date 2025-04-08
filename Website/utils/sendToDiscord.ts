const sendToDiscord = async (name: string, email: string, message: string) => {
  const webhookUrl = process.env.DISCORD_WEBHOOK_URL;

  if (!webhookUrl) {
    throw new Error("Discord webhook URL is missing");
  }

  const payload = {
    username: "WebContact[Bot]",
    embeds: [
      {
        title: "📨 Someone Just Reached Out!",
        description:
          "You have a new message from the contact form!\n\n" +
          "*React with ✅ to acknowledge this message, or ❌ if this is spam.*",
        color: 0x5865f2,
        fields: [
          {
            name: "👤 Name",
            value: `\`${name || "Not provided"}\``,
            inline: true,
          },
          {
            name: "📧 Email",
            value: `\`${email || "Not provided"}\``,
            inline: true,
          },
          {
            name: "⏳ Submitted",
            value: ` *<t:${Math.floor(Date.now() / 1000)}:R>*`,
            inline: false,
          },
          {
            name: "📫 Message",
            value: message
              ? `\`\`\`${message}\`\`\``
              : "_No message provided._",
          },
        ],
        footer: {
          text: "📩 Please review this submission and respond accordingly.",
        },
        timestamp: new Date().toISOString(),
      },
    ],
    allowed_mentions: { parse: [] },
  };

  const response = await fetch(webhookUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error("Failed to send message to Discord");
  }
};

export default sendToDiscord;
