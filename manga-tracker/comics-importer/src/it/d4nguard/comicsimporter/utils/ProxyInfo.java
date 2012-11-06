package it.d4nguard.comicsimporter.utils;

public class ProxyInfo
{
	private final String hostName;
	private final int hostPort;
	private String username;
	private String password;
	private String host;
	private String domain;

	private final boolean useCredentials;

	public ProxyInfo(final String hostName, final int hostPort)
	{
		this.hostName = hostName;
		this.hostPort = hostPort;
		useCredentials = false;
	}

	public ProxyInfo(final String hostName, final int hostPort, final String username, final String password)
	{
		this.hostName = hostName;
		this.hostPort = hostPort;
		this.username = username;
		this.password = password;
		useCredentials = true;
	}

	public ProxyInfo(final String hostName, final int hostPort, final String username, final String password, final String host, final String domain)
	{
		this.hostName = hostName;
		this.hostPort = hostPort;
		this.username = username;
		this.password = password;
		this.host = host;
		this.domain = domain;
		useCredentials = true;
	}

	public String getDomain()
	{
		return domain;
	}

	public String getHost()
	{
		return host;
	}

	public String getHostName()
	{
		return hostName;
	}

	public int getHostPort()
	{
		return hostPort;
	}

	public String getPassword()
	{
		return password;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isUseCredentials()
	{
		return useCredentials;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("ProxyInfo [hostName=");
		builder.append(hostName);
		builder.append(", hostPort=");
		builder.append(hostPort);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", host=");
		builder.append(host);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", useCredentials=");
		builder.append(useCredentials);
		builder.append("]");
		return builder.toString();
	}
}
