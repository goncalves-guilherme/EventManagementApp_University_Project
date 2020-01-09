using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using IdentityServer4.Models;

namespace SilentManager.API
{
    public class Config
    {
        internal static IEnumerable<ApiResource> GetApiResource()
        {
            return new List<ApiResource>
           {
               new ApiResource("thumbIkrApi","Thumb IKR API")
           };
        }

        public static IEnumerable<Client> GetClients()
        {
            return new List<Client>
           {
               new Client{

                   ClientId = "client",
                   AllowedGrantTypes = GrantTypes.ClientCredentials,
                   ClientSecrets =
                   {
                       new Secret("secret".Sha256())
                   },
                   AllowedScopes = { "thumbIkrApi" };
               }
           };
        }
    }
}
