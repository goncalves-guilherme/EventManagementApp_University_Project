using System;
using System.Collections.Generic;

namespace SilentManagerApiServer.Models.DB
{
    public partial class User
    {
        public User()
        {
            Event = new HashSet<Event>();
            Invite = new HashSet<Invite>();
            PersonalSettings = new HashSet<PersonalSettings>();
            Token = new HashSet<Token>();
        }

        public int UserId { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public DateTime? CreateDate { get; set; }

        public virtual ICollection<Event> Event { get; set; }
        public virtual ICollection<Invite> Invite { get; set; }
        public virtual ICollection<PersonalSettings> PersonalSettings { get; set; }
        public virtual ICollection<Token> Token { get; set; }
    }
}
