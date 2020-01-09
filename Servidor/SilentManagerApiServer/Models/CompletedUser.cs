using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.Models.DB
{
    public class CompletedUser
    {
        public int UserId { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public DateTime? CreateDate { get; set; }
    }
}
