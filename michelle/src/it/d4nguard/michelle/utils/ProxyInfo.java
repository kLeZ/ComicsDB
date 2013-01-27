package it.d4nguard.michelle.utils;

import it.d4nguard.michelle.utils.collections.Pair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ProxyInfo
{
	private final String hostName;
	private final int hostPort;
	private String username;
	private String password;
	private String credentialHost;
	private String domain;
	private boolean useCredentials;

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

	public ProxyInfo(final String hostName, final int hostPort, final String username, final String password, final String credentialHost, final String domain)
	{
		this.hostName = hostName;
		this.hostPort = hostPort;
		this.username = username;
		this.password = password;
		this.credentialHost = credentialHost;
		this.domain = domain;
		useCredentials = true;
	}

	public String getDomain()
	{
		return domain;
	}

	public String getCredentialHost()
	{
		return credentialHost;
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

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setCredentialHost(String credentialHost)
	{
		this.credentialHost = credentialHost;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public void setUseCredentials(boolean useCredentials)
	{
		this.useCredentials = useCredentials;
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
		builder.append(", credentialHost=");
		builder.append(credentialHost);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", useCredentials=");
		builder.append(useCredentials);
		builder.append("]");
		return builder.toString();
	}

	public static ProxyInfo getFromEnv()
	{
		Map<String, String> env = System.getenv();
		ProxyInfo ret = null;
		if (env.containsKey("http_proxy") || env.containsKey("HTTP_PROXY"))
		{
			URI http_proxy = null;
			try
			{
				String httpProxy = env.get("http_proxy");
				if (StringUtils.isNullOrWhitespace(httpProxy))
				{
					httpProxy = env.get("HTTP_PROXY");
				}
				http_proxy = new URI(httpProxy);
			}
			catch (URISyntaxException e)
			{
				e.printStackTrace();
			}

			if (http_proxy != null)
			{
				ret = new ProxyInfo(http_proxy.getHost(), http_proxy.getPort());
				if (!StringUtils.isNullOrWhitespace(http_proxy.getAuthority()))
				{
					ret.setUseCredentials(true);
					String authority, pass = null;
					Pair<String, String> userDomain = null;
					authority = http_proxy.getAuthority();
					String[] split = authority.split(":");
					if (split.length == 2)
					{
						pass = split[1];
						userDomain = getUserDomain(split[0]);
					}
					else if (split.length == 1)
					{
						userDomain = getUserDomain(split[0]);
					}
					ret.setUsername(userDomain.getKey());
					if (!StringUtils.isNullOrWhitespace(userDomain.getValue()))
					{
						ret.setDomain(userDomain.getValue());
					}
					if (!StringUtils.isNullOrWhitespace(pass))
					{
						ret.setPassword(pass);
					}
				}
				else
				{
					ret.setUseCredentials(false);
				}
			}
		}
		return ret;
	}

	/**
	 * @param authority
	 */
	private static Pair<String, String> getUserDomain(String authority)
	{
		String user = null;
		String domain = null;
		String[] domusr = authority.split("\\");
		if (domusr.length == 2)
		{
			domain = domusr[0];
			user = domusr[1];
		}
		else if (domusr.length == 1)
		{
			user = domusr[0];
		}
		return new Pair<String, String>(user, domain);
	}
}
