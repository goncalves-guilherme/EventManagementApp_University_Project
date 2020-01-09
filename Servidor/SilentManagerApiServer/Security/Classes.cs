using System;

namespace SilentManager.API.Security
{
    public class User
    {
        public string Email { get; set; }
        public string Password { get; set; }
    }

    public class TokenDto
    {
        public DateTime Expiration { get; set; }
        public string AccessToken { get; set; }
    }
}