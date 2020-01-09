using SilentManager.API.Models;
using SilentManager.API.Models.DB;
using SilentManager.API.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.BusinessLogic.Dtos
{
    public class EventInDto
    {
        public int? EventId { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public CompletedLocation Location { get; set; }
        public int? State { get; set; }
        public int? Radius { get; set; }
        public string Category { get; set; }
    }

    public class TokenInDto
    {
        public string Expiration { get; set; }
        public string AccessToken { get; set; }

        public static TokenDto ConvertFromDTO(TokenInDto tk) {
            DateTime tokenExpiration;
            DateTime.TryParse(tk.Expiration, out tokenExpiration);

            return new TokenDto
            {
                AccessToken = tk.AccessToken,
                Expiration = tokenExpiration
            };
        }
    }

    public class CreateEventDto
    {
        public EventInDto Event { get; set; }
        public TokenInDto Token { get; set; }
    }
}
